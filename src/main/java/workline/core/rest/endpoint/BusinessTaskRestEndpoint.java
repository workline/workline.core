package workline.core.rest.endpoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import loggee.api.Logged;
import vrds.model.RepoItem;
import vrds.model.RepoItemAttribute;
import vrds.model.meta.TODO;
import vrds.model.meta.TODOTag;
import workline.core.api.internal.IProcessTaskHandler;
import workline.core.api.internal.IRepoHandler;

@Path("/task")
@RequestScoped
@Logged
public class BusinessTaskRestEndpoint {
    @Inject
    private IProcessTaskHandler processTaskHandler;
    @Inject
    IRepoHandler repoHandler;

    @GET
    @Path("/readVariable/{businessTaskId}/{variableName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Object readVariable(@PathParam("businessTaskId") Long businessTaskId, @PathParam("variableName") String variableName) {
        Object value = processTaskHandler.readVariable(businessTaskId, variableName);

        return value;
    }

    @TODO(tags = { TODOTag.TRANSACTION })
    @GET
    @Path("/readVariableNames/{businessTaskId}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getBusinessTaskVariableNames(@PathParam("businessTaskId") Long businessTaskId) {
        List<String> businessTaskVariableNames = new ArrayList<>();

        // TODO Must be business task
        RepoItem businessTask = repoHandler.getRepoItem(businessTaskId);

        Set<RepoItemAttribute> repoItemAttributes = businessTask.getRepoItemAttributes();
        for (RepoItemAttribute repoItemAttribute : repoItemAttributes) {
            businessTaskVariableNames.add(repoItemAttribute.getName());
        }

        return businessTaskVariableNames;
    }

    @TODO(tags = { TODOTag.MISSING_IMPLEMENTATION })
    public List<Object> getSelectionList() {
        return null;
    }

    // FIXME Use proper request type
    @GET
    @Path("/writeRepoItemVariable/{businessTaskId}/{variableName}/{repoItemId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Object writeRepoItemVariable(@PathParam("businessTaskId") Long businessTaskId, @PathParam("variableName") String variableName,
            @PathParam("repoItemId") Long repoItemId) {

        processTaskHandler.writeVariable(businessTaskId, variableName, repoItemId);

        return true;
    }

    // FIXME Use proper request type
    @GET
    @Path("/finishTask/{businessTaskId}")
    public void finishTask(@PathParam("businessTaskId") Long businessTaskId) {
        processTaskHandler.finishBusinessTask(businessTaskId);
    }
}
