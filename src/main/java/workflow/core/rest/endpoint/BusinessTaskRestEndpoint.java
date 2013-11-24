package workflow.core.rest.endpoint;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import loggee.api.Logged;
import workflow.core.api.internal.IProcessTaskHandler;

@Path("/task")
@RequestScoped
@Logged
public class BusinessTaskRestEndpoint {
    @Inject
    private IProcessTaskHandler processTaskHandler;

    @GET
    @Path("/readVariable/{businessTaskId}/{variableName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Object readVariable(@PathParam("businessTaskId") Long businessTaskId, @PathParam("variableName") String variableName) {
        return processTaskHandler.readVariable(businessTaskId, variableName);
    }

    // FIXME Use proper request type
    @GET
    @Path("/writeVariable/{businessTaskId}/{variableName}/{value}")
    @Produces(MediaType.APPLICATION_JSON)
    public Object writeVariable(@PathParam("businessTaskId") Long businessTaskId, @PathParam("variableName") String variableName,
            @PathParam("value") String value) {

        processTaskHandler.writeVariable(businessTaskId, variableName, value);

        return true;
    }

    // FIXME Use proper request type
    @GET
    @Path("/finishTask/{businessTaskId}")
    public void finishTask(@PathParam("businessTaskId") Long businessTaskId) {
        processTaskHandler.finishBusinessTask(businessTaskId);
    }
}
