package workflow.tmp.rest;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import loggee.api.Logged;
import vrds.model.RepoItem;
import workflow.repo.service.RepoService;
import workflow.tmp.service.PersonService;

@Path("/repo")
@RequestScoped
@Logged
public class RepoRestService {
    @Inject
    private RepoService repoService;
    @Inject
    private PersonService personService;

    @GET
    @Path("/createRandomPerson/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Long createRandomPerson(@PathParam("name") String name) {
        return personService.createRandomPerson(name);
    }

    @GET
    @Path("/getRepo/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public RepoItem getRepo(@PathParam("id") Long id) {
        return repoService.getRepo(id);
    }
}
