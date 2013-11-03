package workflow.rest.endpoint;

import javax.inject.Inject;

import workflow.processtaskhandler.IProcessTaskHandler;

public class BusinessTaskRestEndpoint {
    @Inject
    private IProcessTaskHandler processTaskHandler;

    public void finishTask(Long businessTaskId, String taskName) {
        processTaskHandler.finishBusinessTask(businessTaskId, taskName);
    }
}
