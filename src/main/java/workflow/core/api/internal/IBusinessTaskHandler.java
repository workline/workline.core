package workflow.core.api.internal;

import vrds.model.RepoItem;

public interface IBusinessTaskHandler extends IBusinessTaskAccessor {
    // TODO Split this interface into 2 parts, one that defines methods that are
    // going to be called by the WorkflowTaskProcessor and another that defines methods
    // that are going to be called by the UI

    void initTask(RepoItem businessTask);

    void finishTask(RepoItem businessTask);

}
