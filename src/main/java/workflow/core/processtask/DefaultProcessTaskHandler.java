package workflow.core.processtask;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import vrds.model.EAttributeType;
import vrds.model.RepoItem;
import vrds.model.attributetype.LongAttributeValueHandler;
import vrds.model.attributetype.RepoItemAttributeValueHandler;
import vrds.model.attributetype.StringAttributeValueHandler;
import vrds.model.meta.TODO;
import workflow.core.api.internal.IBusinessTaskHandler;
import workflow.core.api.internal.IProcessTaskHandler;
import workflow.core.domain.EInputVariableScope;
import workflow.core.domain.ProcessElementVariableDefinition;
import workflow.core.engine.constants.WorklineEngineConstants;
import workflow.core.engine.constants.WorklineEngineQueries;
import workflow.core.meta.SPECIFICATION_REQUIRED;
import workflow.core.repo.IRepoHandler;
import workflow.core.repo.workflow.service.IProcessElementService;
import workflow.core.util.Primary;

// TODO Transaction
@Stateless
public class DefaultProcessTaskHandler implements IProcessTaskHandler {
    @Inject
    private IBusinessTaskHandler businessTaskHandler;
    @Inject
    private IRepoHandler repoHandler;
    @Inject
    private IProcessElementService processElementService;
    @Inject
    @Primary
    private EntityManager entityManager;

    @Override
    public void initProcessTask(Long processRepoItemId, Long workflowTaskId, String taskName) {
        RepoItem process = getProcess(processRepoItemId);
        RepoItem businessTaskDefinition = getBusinessTaskDefinition(taskName);

        RepoItem processTask = createNewProcessTask(processRepoItemId, taskName, workflowTaskId);

        createTaskSpecificProcessVariables(process, processRepoItemId, businessTaskDefinition);

        List<RepoItem> listOfbusinessTasks = createBusinessTasks(taskName, businessTaskDefinition);
        // TODO Add business tasks to process task

        for (RepoItem businessTask : listOfbusinessTasks) {
            repoHandler.addAttribute(businessTask, WorklineEngineConstants.PROCESS, EAttributeType.REPO_ITEM, process);
            repoHandler.addAttribute(businessTask, WorklineEngineConstants.PROCESS_TASK, EAttributeType.REPO_ITEM, processTask);
            businessTaskHandler.initTask(process, businessTask);
        }

    }

    @Override
    public void finishBusinessTask(Long businessTaskId, String taskName) {
        // TODO Auto-generated method stub
        // FIXME This is extremely simplified.
        // Different logic must be called depending on the task.
        if (isLast(businessTaskId)) {
            RepoItem businessTask = getBusinessTask(businessTaskId);
            RepoItem processTask = businessTask.getValue(WorklineEngineConstants.PROCESS_TASK, RepoItemAttributeValueHandler.getInstance());

            finishProcessTask(processTask);
        }
    }

    @Override
    public void readVariable(RepoItem businessTask, String variableName) {
        businessTaskHandler.readVariable(businessTask, variableName);

    }

    @Override
    public void writeVariable(RepoItem businessTask, String variableName, Object value) {
        businessTaskHandler.writeVariable(businessTask, variableName, value);
    }

    private void createTaskSpecificProcessVariables(RepoItem process, Long processRepoItemId, RepoItem businessTaskDefinition) {
        String taskSpecificProcessVariableDefinitionData = businessTaskDefinition.getValue(WorklineEngineConstants.TASK_SPECIFIC_PROCESS_VARIABLES_DEFINITION,
                StringAttributeValueHandler.getInstance());
        List<ProcessElementVariableDefinition> taskSpecificProcessVariableDefinitionList = businessTaskHandler
                .parseIoVariableSourceData(taskSpecificProcessVariableDefinitionData);

        for (ProcessElementVariableDefinition taskSpecificProcessVariableDefinition : taskSpecificProcessVariableDefinitionList) {
            if (taskSpecificProcessVariableDefinition.getInputVariableScope() == EInputVariableScope.PROCESS) {
                processElementService.addVariableToProcessElement(process, taskSpecificProcessVariableDefinition);
            }
        }
    }

    @SPECIFICATION_REQUIRED("How to create business tasks?")
    @TODO("A business task needs to be created for every participating actor/actor group.")
    private List<RepoItem> createBusinessTasks(String taskName, RepoItem businessTaskDefinition) {
        // TODO Auto-generated method stub

        List<RepoItem> businessTasks = new ArrayList<>();

        RepoItem businessTask = new RepoItem();

        repoHandler.addAttribute(businessTask, WorklineEngineConstants.BUSINESS_TASK_DEFINITION, EAttributeType.REPO_ITEM, businessTaskDefinition);
        repoHandler.addAttribute(businessTask, WorklineEngineConstants.FINISHED, EAttributeType.INTEGER);

        repoHandler.persistRepoItem(businessTask);

        businessTasks.add(businessTask);

        return businessTasks;
    }

    private boolean isLast(Long businessTaskId) {
        // TODO Auto-generated method stub
        return true;
    }

    private void finishProcessTask(RepoItem processTask) {
        Long workflowTaskId = processTask.getValue(WorklineEngineConstants.WORKFLOW_TASK_ID, LongAttributeValueHandler.getInstance());

        finishWorkflowTask(workflowTaskId);
    }

    private void finishWorkflowTask(Long workflowTaskId) {
        // FIXME
    }

    private RepoItem createNewProcessTask(Long processRepoItemId, String taskName, Long workflowTaskId) {
        // TODO What is taskName for?
        RepoItem processTask = new RepoItem();

        repoHandler.addAttribute(processTask, WorklineEngineConstants.WORKFLOW_TASK_ID, EAttributeType.INTEGER, workflowTaskId);
        repoHandler.addAttribute(processTask, WorklineEngineConstants.BUSINESS_TASKS, EAttributeType.REPO_ITEM);
        repoHandler.persistRepoItem(processTask);

        return processTask;
    }

    private RepoItem getProcess(Long processRepoItemId) {
        return repoHandler.getRepoItem(processRepoItemId);
    }

    private RepoItem getBusinessTask(Long businessTaskId) {
        return repoHandler.getRepoItem(businessTaskId);
    }

    private RepoItem getBusinessTaskDefinition(String taskName) {
        RepoItem businessTaskDefinition = entityManager.createQuery(WorklineEngineQueries.GET_BUSINESS_TASK_DEFINITION_BY_TASK_NAME, RepoItem.class)
                .getSingleResult();

        return businessTaskDefinition;
    }

}
