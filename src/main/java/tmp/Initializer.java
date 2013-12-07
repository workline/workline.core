package tmp;

import java.util.Arrays;
import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.slf4j.Logger;

import vrds.model.EAttributeType;
import vrds.model.RepoItem;
import workline.core.api.internal.IRepoHandler;
import workline.core.engine.constants.WorklineEngineConstants;
import workline.core.util.Primary;

@Startup
@Singleton
public class Initializer {
    private static final String REPO_ITEM_TYPE_ATTRIBUTE_NAME = "type";

    @Inject
    private Logger logger;

    @Inject
    @Primary
    private EntityManager entityManager;
    @Inject
    private IRepoHandler repoHandler;

    // TODO RepoItems should have an attribute 'types' that tags it as a certain type of RepoItem. E.g. 'accessRight'
    @PostConstruct
    public void init() {
        String nameAttributeName4AccessRight = "name";
        String additionalIOAttributeName4AccessRight = "(Additional input for Access right request Define request)";
        String inputBehaviourSourceAttributeName4AccessRight = "Input behaviour for (Access right request).(Define request)";

        RepoItem testWorklineProcessRepoItem = new RepoItem();
        repoHandler.addAttribute(testWorklineProcessRepoItem, "name", EAttributeType.STRING, "testWorklineProcess");
        entityManager.persist(testWorklineProcessRepoItem);

        String definitionName4DefineRequest = "defineRequest";
        String ioVariableSource4DefineRequest = "accessRight." + additionalIOAttributeName4AccessRight;
        String taskSpecificProcessVariablesDefinition4DefineRequest = "accessRight;REPO::AccessRight;PROCESS;TODO";
        String inputBehaviourSource4DefineRequest = "accessRight." + inputBehaviourSourceAttributeName4AccessRight;
        createTaskDefinition(definitionName4DefineRequest, ioVariableSource4DefineRequest, taskSpecificProcessVariablesDefinition4DefineRequest,
                inputBehaviourSource4DefineRequest);

        String definitionName4Approve = "approve";
        String ioVariableSource4Approve = "";
        String taskSpecificProcessVariablesDefinition4Approve = "";
        String inputBehaviourSourceApprove = "";
        createTaskDefinition(definitionName4Approve, ioVariableSource4Approve, taskSpecificProcessVariablesDefinition4Approve, inputBehaviourSourceApprove);

        String erpName = "ERP";
        String erpType = "accessRight";
        String erpAdditionalIO = "costCenter;REPO::AccessRight;PROCESS;TODO";

        RepoItem erp = createRepoItem(Arrays.asList(new RepoItemAttributeData(REPO_ITEM_TYPE_ATTRIBUTE_NAME, EAttributeType.STRING, erpType),
                new RepoItemAttributeData(nameAttributeName4AccessRight, EAttributeType.STRING, erpName), new RepoItemAttributeData(
                        additionalIOAttributeName4AccessRight, EAttributeType.STRING, erpAdditionalIO)));

        logger.info("ERP id : '" + erp.getId() + "'");
    }

    private RepoItem createRepoItem(Collection<RepoItemAttributeData> attributeDataCollection) {
        RepoItem repoItem = new RepoItem();

        for (RepoItemAttributeData repoItemAttributeData : attributeDataCollection) {
            repoHandler.addAttribute(repoItem, repoItemAttributeData.name, repoItemAttributeData.type, repoItemAttributeData.value);
        }

        entityManager.persist(repoItem);

        return repoItem;
    }

    private void createTaskDefinition(String taskName, String ioVariableSource, String taskSpecificProcessVariablesDefinition, String inputBehaviourSource) {
        RepoItem task = new RepoItem();

        repoHandler.addAttribute(task, WorklineEngineConstants.BUSINESS_TASK_DEFINITION_NAME, EAttributeType.STRING, taskName);
        repoHandler.addAttribute(task, WorklineEngineConstants.IO_VARIABLE_SOURCE, EAttributeType.STRING, ioVariableSource);
        repoHandler.addAttribute(task, WorklineEngineConstants.TASK_SPECIFIC_PROCESS_VARIABLES_DEFINITION, EAttributeType.STRING,
                taskSpecificProcessVariablesDefinition);
        repoHandler.addAttribute(task, WorklineEngineConstants.INPUT_BEHAVIOUR_SOURCE, EAttributeType.STRING, inputBehaviourSource);

        entityManager.persist(task);
    }

    private class RepoItemAttributeData {
        String name;
        EAttributeType type;
        Object value;

        public RepoItemAttributeData(String name, EAttributeType type, Object value) {
            this.name = name;
            this.type = type;
            this.value = value;
        }
    }
}
