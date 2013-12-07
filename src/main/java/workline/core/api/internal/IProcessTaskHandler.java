package workline.core.api.internal;

import workline.core.api.external.IBusinessTaskAPI;

public interface IProcessTaskHandler extends IBusinessTaskAPI {
    void initProcessTask(Long processRepoItemId, Long workflowTaskId, String taskName);
}
