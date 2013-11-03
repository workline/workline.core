package workflow.tmp.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import loggee.api.EMethodLogPolicy;
import loggee.api.Logged;
import workflow.engine.Cmt;
import workflow.engine.Engine;
import workflow.model.ProcessData;

@Path("/workflow")
@RequestScoped
@Logged(booleanMethodLogPolicy = EMethodLogPolicy.REGULAR, logMethodParametersAfterCall = true)
public class EngineRestService {
    @Inject
    @Cmt
    private Engine engine;

    @GET
    @Path("/runEngine")
    @Produces(value = "application/json")
    public boolean runEngine() {
        ProcessData processData = engine.run();
        engine.disposeStatefulKnowledgeSession(processData);
        long start = System.currentTimeMillis();
        engine.proceed(processData);
        long end = System.currentTimeMillis();

        System.err.println("DURATION: " + (end - start));

        return true;
    }

    @GET
    @Path("/strainEngine/{nrOfProcesses}")
    @Produces(value = "application/json")
    public boolean strainEngine(@PathParam("nrOfProcesses") Integer nrOfProcesses) throws InterruptedException, ExecutionException {
        List<Future<ProcessData>> futureProcessDataList = new ArrayList<Future<ProcessData>>();

        Future<ProcessData> futureProcessData;
        for (int i = 0; i < nrOfProcesses; i++) {
            futureProcessData = engine.runAsynchronously();
            futureProcessDataList.add(futureProcessData);
        }

        for (Future<ProcessData> currentFutureProcessData : futureProcessDataList) {
            ProcessData processData = currentFutureProcessData.get();
            engine.disposeStatefulKnowledgeSession(processData);
            long start = System.currentTimeMillis();
            engine.proceed(processData);
            long end = System.currentTimeMillis();

            // System.err.println("DURATION: " + (end - start));
        }

        return true;
    }

    @GET
    @Path("/runEngine/{sessionId}/{processInstanceId}/{workItemId}/{tryTrigger}")
    @Produces(value = "application/json")
    public boolean resumeEngine(@PathParam("sessionId") Integer sessionId, @PathParam("processInstanceId") Long processInstanceId,
            @PathParam("workItemId") Long workItemId, @PathParam("tryTrigger") Boolean tryTrigger) {

        engine.resume(sessionId, processInstanceId, workItemId, tryTrigger);

        return true;
    }
}
