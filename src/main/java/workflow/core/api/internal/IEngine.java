package workflow.core.api.internal;

import workflow.core.domain.ProcessData;

public interface IEngine {
    ProcessData startProcess(String processName);

    void completeWorkItem(Integer sessionId, Long processInstanceId, Long workItemId);
}
