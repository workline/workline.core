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
import workflow.core.api.internal.IBusinessTaskHandler;
import workflow.core.api.internal.IProcessTaskHandler;
import workflow.core.engine.constants.WorklineEngineConstants;
import workflow.core.engine.constants.WorklineEngineQueries;
import workflow.core.meta.SPECIFICATION_REQUIRED;
import workflow.core.repo.IRepoHandler;
import workflow.core.util.Primary;

// TODO Transaction
@Stateless
public class DefaultProcessTaskHandler implements IProcessTaskHandler {
    @Inject
    private IBusinessTaskHandler businessTaskHandler;
    @Inject
    private IRepoHandler repoHandler;
    @Inject
    @Primary
    private EntityManager entityManager;

    @Override
    public void initProcessTask(Long processRepoItemId, Long workflowTaskId, String taskName) {
        RepoItem processTask = createNewProcessTask(processRepoItemId, taskName);
        repoHandler.addAttribute(processTask, WorklineEngineConstants.WORKFLOW_TASK_ID, EAttributeType.INTEGER, workflowTaskId);

        List<RepoItem> listOfbusinessTasks = createBusinessTasks(taskName);
        // TODO Add business tasks to process task

        for (RepoItem businessTask : listOfbusinessTasks) {
            repoHandler.addAttribute(businessTask, WorklineEngineConstants.PROCESS_TASK, EAttributeType.REPO_ITEM, processTask);
            businessTaskHandler.initTask(businessTask);
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

    @SPECIFICATION_REQUIRED("How to create business tasks?")
    private List<RepoItem> createBusinessTasks(String taskName) {
        // TODO Auto-generated method stub
        // businessTask has an attribute 'finished'

        RepoItem businessTaskDefinition = getBusinessTaskDefinition(taskName);

        List<RepoItem> businessTasks = new ArrayList<>();

        RepoItem businessTask = new RepoItem();

        repoHandler.addAttribute(businessTask, WorklineEngineConstants.BUSINESS_TASK_DEFINITION, EAttributeType.REPO_ITEM, businessTaskDefinition);
        repoHandler.addAttribute(businessTask, WorklineEngineConstants.FINISHED, EAttributeType.INTEGER);

        repoHandler.persistRepoItem(businessTask);

        businessTasks.add(businessTask);

        return businessTasks;
    }

    private RepoItem getBusinessTaskDefinition(String taskName) {
        RepoItem businessTaskDefinition = entityManager.createQuery(WorklineEngineQueries.GET_BUSINESS_TASK_DEFINITION_BY_TASK_NAME, RepoItem.class)
                .getSingleResult();

        return businessTaskDefinition;
    }

    private RepoItem getBusinessTask(Long businessTaskId) {
        return repoHandler.getRepoItem(businessTaskId);
    }

    private boolean isLast(Long businessTaskId) {
        // TODO Auto-generated method stub
        return true;
    }

    private RepoItem createNewProcessTask(Long processRepoItemId, String taskName) {
        RepoItem processTask = new RepoItem();

        repoHandler.addAttribute(processTask, WorklineEngineConstants.BUSINESS_TASKS, EAttributeType.REPO_ITEM);
        repoHandler.persistRepoItem(processTask);

        return processTask;
    }

    private void finishProcessTask(RepoItem processTask) {
        Long workflowTaskId = processTask.getValue(WorklineEngineConstants.WORKFLOW_TASK_ID, LongAttributeValueHandler.getInstance());

        finishWorkflowTask(workflowTaskId);
    }

    private void finishWorkflowTask(Long workflowTaskId) {
        // TODO
    }

}
