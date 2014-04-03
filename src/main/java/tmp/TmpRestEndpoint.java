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
    @Path("/setStringValue/{repoItemId}/{inheritenceSourceRepoItemId}/{attributeName}/{stringValue}")
    @Produces(MediaType.APPLICATION_JSON)
    public Object setStringValue(@PathParam("repoItemId") Long repoItemId, @PathParam("inheritenceSourceRepoItemId") Long inheritenceSourceRepoItemId,
            @PathParam("attributeName") String attributeName, @PathParam("stringValue") String stringValue) {

        tmpService.setStringValue(repoItemId, inheritenceSourceRepoItemId, attributeName, stringValue);

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

    @GET
    @Path("/putIntoCache/{key}/{value}")
    public Object putIntoCache(@PathParam("key") String key, @PathParam("value") String value) {
        tmpService.putIntoCache(key, value);

        return true;
    }

    @GET
    @Path("/retrieveFromCache/{key}")
    public Object retrieveFromCache(@PathParam("key") String key) {
        return tmpService.retrieveFromCache(key);
    }
}
