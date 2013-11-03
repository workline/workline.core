package workflow.engine;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import javax.annotation.Resource;
import javax.ejb.AsyncResult;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.TransactionManager;

import loggee.api.Logged;

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
import org.drools.runtime.process.WorkflowProcessInstance;

import workflow.domain.Person;
import workflow.history.HistoryLogger;
import workflow.model.ProcessData;
import workflow.tmp.workitem.TmpWorkItemHandler;
import workflow.util.Primary;
import workflow.util.WorkflowHistory;

@Logged
@Umt
@ApplicationScoped
public class UMTEngine implements Engine {
    private static final String PROCESS_DEFINITION_DIRECTORY_PATH = "bpmn/";
    private static final String PROCESS_NAME = "MainProcess5";
    private static final String PROCESS_EXTENSION = "bpmn2";

    @Inject
    @Primary
    private EntityManager entityManager;
    @Inject
    @WorkflowHistory
    private EntityManager workflowHistoryEntityManager;

    @Resource(mappedName = "java:jboss/TransactionManager")
    private TransactionManager transactionManager;

    private String workItemName = "TmpWorkItem";

    @Override
    public Future<ProcessData> runAsynchronously() {
        ProcessData processData = run();
        return new AsyncResult<ProcessData>(processData);
    }

    @Override
    public ProcessData run() {
        ThiefEntityManagerFactory thiefEntityManagerFactory = new ThiefEntityManagerFactory(entityManager.getEntityManagerFactory());

        int sessionId;
        long processInstanceId;
        Map<String, Object> parameters;
        KnowledgeBuilder knowledgeBuilder;
        KnowledgeBase knowledgeBase;
        Environment env;
        StatefulKnowledgeSession statefulKnowledgeSession;
        TmpWorkItemHandler handler;
        ProcessInstance processInstance;

        handler = new TmpWorkItemHandler(thiefEntityManagerFactory);

        Person person = new Person();
        person.setName("Joe");
        Person person2 = new Person();
        person2.setName("Adam");

        parameters = new HashMap<String, Object>();
        int nrOfPersons = 100;
        for (int i = 0; i < nrOfPersons; i++) {
            person = new Person();
            person.setName("PersoPersoPersoPersoPersoPersoPersoPersoPersoPerso");
            parameters.put(i + "", person);
        }

        env = buildEnvironment(thiefEntityManagerFactory);

        // Build knowledge base
        knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        knowledgeBuilder.add(ResourceFactory.newClassPathResource(PROCESS_DEFINITION_DIRECTORY_PATH + PROCESS_NAME + "." + PROCESS_EXTENSION),
                ResourceType.BPMN2);
        knowledgeBase = knowledgeBuilder.newKnowledgeBase();

        // Create knowledge session
        // StatefulKnowledgeSession statefulKnowledgeSession = knowledgeBase.newStatefulKnowledgeSession();

        statefulKnowledgeSession = JPAKnowledgeService.newStatefulKnowledgeSession(knowledgeBase, null, env);
        sessionId = statefulKnowledgeSession.getId();

        // System.err.println("Session id: " + sessionId);

        // Add history logger
        new HistoryLogger(statefulKnowledgeSession, workflowHistoryEntityManager.getEntityManagerFactory(), transactionManager, parameters);

        // Add logger to knowledge session
        // KnowledgeRuntimeLogger logger = KnowledgeRuntimeLoggerFactory.newFileLogger(statefulKnowledgeSession, "d:/tmp/workflow.log");

        // Put stuff into knowledge session
        statefulKnowledgeSession.setGlobal("person2", person2);

        statefulKnowledgeSession.getWorkItemManager().registerWorkItemHandler(workItemName, handler);

        // Start process
        processInstance = statefulKnowledgeSession.createProcessInstance(PROCESS_NAME, parameters);
        processInstanceId = processInstance.getId();

        // System.err.println("Process instance id: " + processInstanceId);

        statefulKnowledgeSession.startProcessInstance(processInstanceId);

        // // Complete task(s)
        // workItemId = handler.getWorkItemId();
        //
        // // System.err.println("Work item id: " + workItemId);
        //
        // statefulKnowledgeSession.getWorkItemManager().completeWorkItem(workItemId, null);

        // Signal event
        statefulKnowledgeSession.signalEvent("RESUME", null, processInstanceId);

        ProcessData processData = new ProcessData();
        processData.setSessionId(sessionId);
        processData.setProcessInstanceId(processInstanceId);
        processData.setWorkItemId(handler.getWorkItemId());
        processData.setStatefulKnowledgeSession(statefulKnowledgeSession);

        return processData;
    }

    @Override
    public void proceed(ProcessData processData) {
        ThiefEntityManagerFactory thiefEntityManagerFactory = new ThiefEntityManagerFactory(entityManager.getEntityManagerFactory());

        int sessionId = processData.getSessionId();
        long processInstanceId = processData.getProcessInstanceId();
        Map<String, Object> parameters;
        KnowledgeBuilder knowledgeBuilder;
        KnowledgeBase knowledgeBase;
        Environment env;
        StatefulKnowledgeSession statefulKnowledgeSession;
        TmpWorkItemHandler handler;
        ProcessInstance processInstance;
        Long workItemId;

        handler = new TmpWorkItemHandler(thiefEntityManagerFactory);

        Person person = new Person();
        person.setName("Joe");
        Person person2 = new Person();
        person2.setName("Adam");

        parameters = new HashMap<String, Object>();
        parameters.put("person", person);

        env = buildEnvironment(thiefEntityManagerFactory);

        // Rebuild
        knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        knowledgeBuilder.add(ResourceFactory.newClassPathResource(PROCESS_DEFINITION_DIRECTORY_PATH + PROCESS_NAME + ".bpmn2"), ResourceType.BPMN2);
        knowledgeBase = knowledgeBuilder.newKnowledgeBase();
        statefulKnowledgeSession = JPAKnowledgeService.loadStatefulKnowledgeSession(sessionId, knowledgeBase, null, env);

        statefulKnowledgeSession.getWorkItemManager().registerWorkItemHandler(workItemName, handler);

        new HistoryLogger(statefulKnowledgeSession, workflowHistoryEntityManager.getEntityManagerFactory(), transactionManager, parameters);

        processInstance = statefulKnowledgeSession.getProcessInstance(processInstanceId);
        // if (processInstance instanceof ProcessInstanceImpl) {
        // ProcessInstanceImpl processInstanceImpl = (ProcessInstanceImpl) processInstance;
        //
        // InternalKnowledgeRuntime knowledgeRuntime = processInstanceImpl.getKnowledgeRuntime();
        // if (knowledgeRuntime == null) {
        // processInstanceImpl.setKnowledgeRuntime((InternalKnowledgeRuntime) statefulKnowledgeSession);
        // }
        // }

        // statefulKnowledgeSession.signalEvent("Trigger", null);

        // Signal same event again
        statefulKnowledgeSession.signalEvent("RESUME", null, processInstanceId);

        // Complete task(s)
        workItemId = processData.getWorkItemId();

        // System.err.println("Work item id: " + workItemId);

        statefulKnowledgeSession.getWorkItemManager().completeWorkItem(workItemId, null);

        // Check various stuff after process has finished
        // System.err.println("process variable 'person': " + ((WorkflowProcessInstance) processInstance).getVariable("person"));
        // System.err.println("process parameter 'person': " + parameters.get("person"));
        // System.err.println("global 'person2': " + statefulKnowledgeSession.getGlobal("person2"));

        // org.jbpm.process.instance.ProcessInstance jbpmProcessInstance = (org.jbpm.process.instance.ProcessInstance) processInstance;
        // VariableScopeInstance variableContextInstance = (VariableScopeInstance) jbpmProcessInstance.getContextInstance(VariableScope.VARIABLE_SCOPE);
        // Map<String, Object> variables = variableContextInstance.getVariables();
        //
        // // System.err.println("process variables: " + variables.entrySet());

        // final long processId = processInstance.getId();
        // Map<String, Object> variables2 = statefulKnowledgeSession.execute(
        // new GenericCommand<Map<String, Object>>() {
        // private static final long serialVersionUID = 1L;
        //
        // public Map<String, Object> execute(Context context) {
        // StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();
        // org.jbpm.process.instance.ProcessInstance processInstance = (org.jbpm.process.instance.ProcessInstance) ksession.getProcessInstance(processId);
        // VariableScopeInstance variableScope = (VariableScopeInstance) processInstance.getContextInstance(VariableScope.VARIABLE_SCOPE);
        // Map<String, Object> variables = variableScope.getVariables();
        // return variables;
        // }
        // }
        // );
        //
        // // System.err.println("process variables via command: " + variables2.entrySet());

        // logger.close();
    }

    @Override
    public void resume(Integer sessionId, Long processInstanceId, Long workItemId, Boolean tryTrigger) {
        ThiefEntityManagerFactory thiefEntityManagerFactory = new ThiefEntityManagerFactory(entityManager.getEntityManagerFactory());
        String workItemName = "TmpWorkItem";

        Map<String, Object> parameters;
        KnowledgeBuilder knowledgeBuilder;
        KnowledgeBase knowledgeBase;
        Environment env;
        StatefulKnowledgeSession statefulKnowledgeSession;
        TmpWorkItemHandler handler;
        ProcessInstance processInstance;

        handler = new TmpWorkItemHandler(thiefEntityManagerFactory);

        Person person = new Person();
        person.setName("Joe");
        Person person2 = new Person();
        person2.setName("Adam");

        parameters = new HashMap<String, Object>();
        parameters.put("person", person);

        env = buildEnvironment(thiefEntityManagerFactory);

        // Rebuild
        knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        knowledgeBuilder.add(ResourceFactory.newClassPathResource(PROCESS_DEFINITION_DIRECTORY_PATH + PROCESS_NAME + ".bpmn2"), ResourceType.BPMN2);
        knowledgeBase = knowledgeBuilder.newKnowledgeBase();
        statefulKnowledgeSession = JPAKnowledgeService.loadStatefulKnowledgeSession(sessionId, knowledgeBase, null, env);

        statefulKnowledgeSession.getWorkItemManager().registerWorkItemHandler(workItemName, handler);

        new HistoryLogger(statefulKnowledgeSession, workflowHistoryEntityManager.getEntityManagerFactory(), transactionManager, parameters);

        processInstance = statefulKnowledgeSession.getProcessInstance(processInstanceId);

        statefulKnowledgeSession.startProcessInstance(processInstanceId);

        if (tryTrigger) {
            statefulKnowledgeSession.signalEvent("RESUME", null);
        }

        if (workItemId > 0) {
            // Complete task(s)
            statefulKnowledgeSession.getWorkItemManager().completeWorkItem(workItemId, null);
        } else {
            statefulKnowledgeSession.getWorkItemManager().completeWorkItem(handler.getWorkItemId(), null);
        }

        // Check various stuff after process has finished
        // System.err.println("process variable 'person': " + ((WorkflowProcessInstance) processInstance).getVariable("person"));
        // System.err.println("process parameter 'person': " + parameters.get("person"));
        // System.err.println("global 'person2': " + statefulKnowledgeSession.getGlobal("person2"));

        // org.jbpm.process.instance.ProcessInstance jbpmProcessInstance = (org.jbpm.process.instance.ProcessInstance) processInstance;
        // VariableScopeInstance variableContextInstance = (VariableScopeInstance) jbpmProcessInstance.getContextInstance(VariableScope.VARIABLE_SCOPE);
        // Map<String, Object> variables = variableContextInstance.getVariables();
        //
        // // System.err.println("process variables: " + variables.entrySet());

        // final long processId = processInstance.getId();
        // Map<String, Object> variables2 = statefulKnowledgeSession.execute(
        // new GenericCommand<Map<String, Object>>() {
        // private static final long serialVersionUID = 1L;
        //
        // public Map<String, Object> execute(Context context) {
        // StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();
        // org.jbpm.process.instance.ProcessInstance processInstance = (org.jbpm.process.instance.ProcessInstance) ksession.getProcessInstance(processId);
        // VariableScopeInstance variableScope = (VariableScopeInstance) processInstance.getContextInstance(VariableScope.VARIABLE_SCOPE);
        // Map<String, Object> variables = variableScope.getVariables();
        // return variables;
        // }
        // }
        // );
        //
        // // System.err.println("process variables via command: " + variables2.entrySet());

        // logger.close();
    }

    @Override
    public void disposeStatefulKnowledgeSession(ProcessData processData) {
        processData.getStatefulKnowledgeSession().dispose();

    }

    private Environment buildEnvironment(ThiefEntityManagerFactory thiefEntityManagerFactory) {
        Environment env;
        env = EnvironmentFactory.newEnvironment();
        env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, thiefEntityManagerFactory);
        env.set(EnvironmentName.TRANSACTION_MANAGER, transactionManager);
        return env;
    }
}
