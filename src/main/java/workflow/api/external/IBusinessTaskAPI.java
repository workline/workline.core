package workflow.api.external;

import workflow.api.internal.IBusinessTaskAccessor;

public interface IBusinessTaskAPI extends IBusinessTaskAccessor {
    void finishBusinessTask(Long businessTaskId, String taskName);
}
