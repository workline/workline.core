package workline.core.rest.endpoint;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import loggee.api.Logged;
import workline.core.api.internal.IEngine;

@Path("/engine")
@RequestScoped
@Logged
public class EngineRestEndpoint {
    @Inject
    private IEngine engine;

    @GET
    @Path("/start/{processName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Object startProcess(@PathParam("processName") String processName) {
        engine.startProcess(processName);

        return true;
    }

    @GET
    @Path("/complete/{sessionId}/{processInstanceId}/{workItemId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Object completeWorkItem(@PathParam("sessionId") Integer sessionId, @PathParam("processInstanceId") Long processInstanceId,
            @PathParam("workItemId") Long workItemId) {
        engine.completeWorkItem(sessionId, processInstanceId, workItemId);

        return true;
    }
}
