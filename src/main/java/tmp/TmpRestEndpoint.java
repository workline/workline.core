package tmp;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import loggee.api.Logged;

@Path("/tmp")
@RequestScoped
@Logged
public class TmpRestEndpoint {
    @Inject
    private TmpService tmpService;

    @GET
    @Path("/setStringValue/{repoItemId}/{benefactorRepoItemId}/{attributeName}/{stringValue}")
    @Produces(MediaType.APPLICATION_JSON)
    public Object setStringValue(@PathParam("repoItemId") Long repoItemId, @PathParam("benefactorRepoItemId") Long benefactorRepoItemId,
            @PathParam("attributeName") String attributeName, @PathParam("stringValue") String stringValue) {

        tmpService.setStringValue(repoItemId, benefactorRepoItemId, attributeName, stringValue);

        return true;
    }

    @GET
    @Path("/setStringValue/{repoItemId}/{attributeName}/{stringValue}")
    @Produces(MediaType.APPLICATION_JSON)
    public Object setStringValue(@PathParam("repoItemId") Long repoItemId, @PathParam("attributeName") String attributeName,
            @PathParam("stringValue") String stringValue) {

        tmpService.setStringValue(repoItemId, attributeName, stringValue);

        return true;
    }
}
