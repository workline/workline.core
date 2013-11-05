package workflow.core.rest.endpoint;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import loggee.api.Logged;
import workflow.core.api.internal.IEngine;

@Path("/engine")
@RequestScoped
@Logged
public class EngineRestEndpoint {
    @Inject
    private IEngine engine;

    @GET
    @Path("/start/{processName}")
    public void startProcess(@PathParam("processName") String processName) {
        engine.startProcess(processName);
    }

    @GET
    @Path("/complete/{sessionId}/{processInstanceId}/{workItemId}")
    public void completeWorkItem(@PathParam("sessionId") Integer sessionId, @PathParam("processInstanceId") Long processInstanceId,
            @PathParam("workItemId") Long workItemId) {
        engine.completeWorkItem(sessionId, processInstanceId, workItemId);
    }
}
