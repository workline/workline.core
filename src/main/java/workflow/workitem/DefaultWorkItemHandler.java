package workflow.workitem;

import javax.inject.Inject;

import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;

import workflow.processtaskhandler.IProcessTaskHandler;

public class DefaultWorkItemHandler implements WorkItemHandler {
    // Processor scoped
    private static final String PROCESS_REPO_ITEM_ID = "processRepoItemId";

    // Task scoped
    private static final String TASK_NAME = "taskName";

    @Inject
    private IProcessTaskHandler processTaskHandler;

    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        String taskName = (String) workItem.getParameter(TASK_NAME);

        Long processRepoItemId = (Long) workItem.getParameter(PROCESS_REPO_ITEM_ID);
        long workItemId = workItem.getId();

        processTaskHandler.initProcessTask(processRepoItemId, workItemId, taskName);

    }

    private String getProcessName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        // TODO Auto-generated method stub

    }

}
