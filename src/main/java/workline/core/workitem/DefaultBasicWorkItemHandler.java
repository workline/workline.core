package workline.core.workitem;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import loggee.api.Logged;

import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemManager;

import vrds.model.meta.TODO;
import workline.core.api.internal.IBasicWorkItemHandler;
import workline.core.api.internal.IProcessTaskHandler;
import workline.core.engine.constants.WorklineEngineConstants;
import workline.core.meta.BEWARE;
import workline.core.meta.BEWARETag;

@BEWARE(tags = { BEWARETag.SCOPE })
@Logged
@RequestScoped
public class DefaultBasicWorkItemHandler implements IBasicWorkItemHandler {
    @TODO("Is this thread-safe?")
    private Long workItemId;

    @Inject
    private IProcessTaskHandler processTaskHandler;

    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        String taskName = (String) workItem.getParameter(WorklineEngineConstants.TASK_NAME);

        Long processRepoItemId = (Long) workItem.getParameter(WorklineEngineConstants.PROCESS_REPO_ITEM_ID);
        long workItemId = workItem.getId();

        processTaskHandler.initProcessTask(processRepoItemId, workItemId, taskName);

        this.workItemId = workItemId;
    }

    @Override
    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        // TODO Auto-generated method stub

    }

    @Override
    public Long getWorkItemId() {
        return workItemId;
    }

}
