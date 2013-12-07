package workline.core.api.internal;

import workline.core.domain.ProcessData;

public interface IEngine {
    ProcessData startProcess(String processName);

    void completeWorkItem(Integer sessionId, Long processInstanceId, Long workItemId);
}
