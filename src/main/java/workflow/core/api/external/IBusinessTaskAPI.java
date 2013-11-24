package workflow.core.api.external;

import workflow.core.api.internal.IBusinessTaskAccessor;

public interface IBusinessTaskAPI extends IBusinessTaskAccessor {
    void finishBusinessTask(Long businessTaskId);
}
