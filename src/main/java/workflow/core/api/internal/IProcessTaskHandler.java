package workflow.core.api.internal;

import workflow.core.api.external.IBusinessTaskAPI;

public interface IProcessTaskHandler extends IBusinessTaskAPI {
    void initProcessTask(Long processRepoItemId, Long workflowTaskId, String taskName);
}
