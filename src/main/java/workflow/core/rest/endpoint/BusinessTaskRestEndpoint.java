package workflow.core.rest.endpoint;

import javax.inject.Inject;

import workflow.core.api.internal.IProcessTaskHandler;

// TODO
public class BusinessTaskRestEndpoint {
    @Inject
    private IProcessTaskHandler processTaskHandler;

    public void finishTask(Long businessTaskId, String taskName) {
        processTaskHandler.finishBusinessTask(businessTaskId, taskName);
    }
}
