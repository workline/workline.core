package workline.core.api.external;

import workline.core.api.internal.IBusinessTaskAccessor;

public interface IBusinessTaskAPI extends IBusinessTaskAccessor {
    void finishBusinessTask(Long businessTaskId);
}
