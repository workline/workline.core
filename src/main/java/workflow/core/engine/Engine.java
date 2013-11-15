package workflow.core.engine;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.drools.KnowledgeBase;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.impl.EnvironmentFactory;
import org.drools.io.ResourceFactory;
import org.drools.persistence.jpa.JPAKnowledgeService;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.jbpm.persistence.JpaProcessPersistenceContextManager;
import org.jbpm.persistence.jta.ContainerManagedTransactionManager;
import org.slf4j.Logger;

import vrds.model.RepoItem;
import workflow.core.api.internal.IBasicWorkItemHandler;
import workflow.core.api.internal.IEngine;
import workflow.core.domain.ProcessData;
import workflow.core.engine.constants.WorklineEngineConstants;
import workflow.core.meta.SPECIFICATION_REQUIRED;
import workflow.core.util.Primary;
import workflow.core.workitem.DefaultBasicWorkItemHandler;

@SPECIFICATION_REQUIRED("Issued AccessRight?")
@Stateless
public class Engine implements IEngine {
    private static final String PROCESS_DEFINITION_DIRECTORY_PATH = "bpmn/";
    private static final String PROCESS_NAME = "access_right_request_2";
    private static final String PROCESS_EXTENSION = "bpmn2";
    private static final String BASIC_WORK_ITEM_NAME = "TmpWorkItem";

    @Inject
    @Primary
    private EntityManager entityManager;

    @Inject
    private IBasicWorkItemHandler basicWorkItemHandler;

    @Inject
    private Logger logger;

    private KnowledgeBuilder knowledgeBuilder;
    private KnowledgeBase knowledgeBase;

    @PostConstruct
    public void init() {
        long start = System.currentTimeMillis();

        buildKnowledgeBase();

        long end = System.currentTimeMillis();
        logger.info(this.getClass().getSimpleName() + " init duration: " + (end - start) + " ms");
    }

    @Override
    public ProcessData startProcess(String processName) {
        Environment environment;
        StatefulKnowledgeSession statefulKnowledgeSession;
        Map<String, Object> processParameters;
        ProcessInstance processInstance;

        environment = createEnvironment();

        statefulKnowledgeSession = JPAKnowledgeService.newStatefulKnowledgeSession(knowledgeBase, null, environment);
        statefulKnowledgeSession.getWorkItemManager().registerWorkItemHandler(BASIC_WORK_ITEM_NAME, basicWorkItemHandler);

        RepoItem processRepoItem = createProcessRepoItem();

        processParameters = new HashMap<String, Object>();
        processParameters.put(WorklineEngineConstants.PROCESS_REPO_ITEM_ID, processRepoItem.getId());

        processInstance = statefulKnowledgeSession.createProcessInstance(PROCESS_NAME, processParameters);
        long processInstanceId = processInstance.getId();

        statefulKnowledgeSession.startProcessInstance(processInstanceId);

        ProcessData processData = new ProcessData();
        processData.setSessionId(statefulKnowledgeSession.getId());
        processData.setProcessInstanceId(processInstanceId);
        processData.setWorkItemId(basicWorkItemHandler.getWorkItemId());
        processData.setStatefulKnowledgeSession(statefulKnowledgeSession);

        return processData;
    }

    @Override
    public void completeWorkItem(Integer sessionId, Long processInstanceId, Long workItemId) {
        Environment environment;
        StatefulKnowledgeSession statefulKnowledgeSession;

        environment = createEnvironment();

        statefulKnowledgeSession = JPAKnowledgeService.loadStatefulKnowledgeSession(sessionId, knowledgeBase, null, environment);

        basicWorkItemHandler = new DefaultBasicWorkItemHandler();
        statefulKnowledgeSession.getWorkItemManager().registerWorkItemHandler(BASIC_WORK_ITEM_NAME, basicWorkItemHandler);

        statefulKnowledgeSession.getWorkItemManager().completeWorkItem(workItemId, null);
    }

    private void buildKnowledgeBase() {
        knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        knowledgeBuilder.add(ResourceFactory.newClassPathResource(PROCESS_DEFINITION_DIRECTORY_PATH + PROCESS_NAME + "." + PROCESS_EXTENSION),
                ResourceType.BPMN2);
        knowledgeBase = knowledgeBuilder.newKnowledgeBase();
    }

    private Environment createEnvironment() {
        Environment environment;

        environment = EnvironmentFactory.newEnvironment();
        environment.set(EnvironmentName.ENTITY_MANAGER_FACTORY, entityManager.getCriteriaBuilder());
        environment.set(EnvironmentName.TRANSACTION_MANAGER, new ContainerManagedTransactionManager());
        environment.set(EnvironmentName.PERSISTENCE_CONTEXT_MANAGER, new JpaProcessPersistenceContextManager(environment));

        return environment;
    }

    // TODO Maybe task specific process variables with default values should be created at this point
    private RepoItem createProcessRepoItem() {
        RepoItem processRepoItem = new RepoItem();

        entityManager.persist(processRepoItem);

        return processRepoItem;
    }

}
