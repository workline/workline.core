package tmp;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import vrds.model.EAttributeType;
import vrds.model.RepoItem;
import workflow.core.api.internal.IRepoHandler;
import workflow.core.engine.constants.WorklineEngineConstants;
import workflow.core.util.Primary;

@Startup
@Singleton
public class Initializer {
    @Inject
    @Primary
    private EntityManager entityManager;
    @Inject
    private IRepoHandler repoHandler;

    // TODO RepoItems should have an attribute 'types' that tags it as a certain type of RepoItem. E.g. 'accessRight'
    @PostConstruct
    public void init() {
        RepoItem testWorklineProcessRepoItem = new RepoItem();
        repoHandler.addAttribute(testWorklineProcessRepoItem, "name", EAttributeType.STRING, "testWorklineProcess");
        entityManager.persist(testWorklineProcessRepoItem);

        String definitionName4DefineRequest = "defineRequestDefinition";
        String ioVariableSource4DefineRequest = "accessRight.(Additional input for Access right request Define request)";
        String taskSpecificProcessVariablesDefinition4DefineRequest = "accessRight;Repo::AccessRight;Process;existing accessRight";
        String inputBehaviourSource4DefineRequest = "accessRight.Input behaviour for (Access right request).(Define request)";
        createTaskDefinition(definitionName4DefineRequest, ioVariableSource4DefineRequest, taskSpecificProcessVariablesDefinition4DefineRequest,
                inputBehaviourSource4DefineRequest);

        String definitionName4Approve = "approveDefinition";
        String ioVariableSource4Approve = "";
        String taskSpecificProcessVariablesDefinition4Approve = "";
        String inputBehaviourSourceApprove = "";
        createTaskDefinition(definitionName4Approve, ioVariableSource4Approve, taskSpecificProcessVariablesDefinition4Approve, inputBehaviourSourceApprove);
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
}
