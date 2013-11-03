package workflow.processtaskhandler;

import workflow.api.external.IBusinessTaskAPI;

public interface IProcessTaskHandler extends IBusinessTaskAPI {
    void initProcessTask(Long processRepoItemId, Long workflowTaskId, String taskName);
}
