package workflow.processtaskhandler;

import java.util.HashSet;
import java.util.List;

import javax.inject.Inject;

import vrds.model.EAttributeType;
import vrds.model.RepoItem;
import vrds.model.RepoItemAttribute;
import vrds.model.attributetype.LongAttributeValueHandler;
import vrds.model.attributetype.RepoItemAttributeValueHandler;
import workflow.businesstask.IBusinessTaskHandler;

// TODO Transaction
public class DefaultProcessTaskHandler implements IProcessTaskHandler {
    private static final String WORKFLOW_TASK_ID = "workflowTaskId";
    private static final String BUSINESS_TASKS = "businessTasks";
    private static final String PROCESS_TASK = "processTask";

    @Inject
    private IBusinessTaskHandler businessTaskHandler;

    @Override
    public void initProcessTask(Long processRepoItemId, Long workflowTaskId, String taskName) {
        RepoItem processTask = createNewProcessTask(processRepoItemId, taskName);
        // TODO Add workflowTaskId as value of the WORKLOW_TASK_ID attribute of the processTask

        List<RepoItem> listOfbusinessTasks = createBusinessTasks();
        // TODO Add business tasks to process task

        for (RepoItem businessTask : listOfbusinessTasks) {
            // TODO Add processTask as attribute value to businessTask
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
            RepoItem processTask = businessTask.getValue(PROCESS_TASK, RepoItemAttributeValueHandler.getInstance());

            finishProcessTask(processTask);
        }
    }

    private List<RepoItem> createBusinessTasks() {
        // TODO Auto-generated method stub
        // businessTask has an attribute 'finished'
        return null;
    }

    private RepoItem getBusinessTask(Long businessTaskId) {
        // TODO Auto-generated method stub
        return null;
    }

    private boolean isLast(Long businessTaskId) {
        // TODO Auto-generated method stub
        return false;
    }

    private RepoItem createNewProcessTask(Long processRepoItemId, String taskName) {
        // TODO Persist processTask
        RepoItem processTask = new RepoItem();

        HashSet<RepoItemAttribute> attributesOfProcessTask = new HashSet<>();

        RepoItemAttribute businessTasksOfProcessTask = new RepoItemAttribute();
        businessTasksOfProcessTask.setNameAndType(BUSINESS_TASKS, EAttributeType.REPO_ITEM);
        attributesOfProcessTask.add(businessTasksOfProcessTask);

        processTask.setRepoItemAttributes(attributesOfProcessTask);

        return processTask;
    }

    private void finishProcessTask(RepoItem processTask) {
        Long workflowTaskId = processTask.getValue(WORKFLOW_TASK_ID, LongAttributeValueHandler.getInstance());

        finishWorkflowTask(workflowTaskId);
    }

    private void finishWorkflowTask(Long workflowTaskId) {
        // TODO
    }

}
