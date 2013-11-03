package workflow.engine;

import java.util.concurrent.Future;

import workflow.model.ProcessData;

public interface Engine {

    ProcessData run();

    Future<ProcessData> runAsynchronously();

    void proceed(ProcessData processData);

    void resume(Integer sessionId, Long processInstanceId, Long workItemId, Boolean tryTrigger);

    void disposeStatefulKnowledgeSession(ProcessData processData);

}
