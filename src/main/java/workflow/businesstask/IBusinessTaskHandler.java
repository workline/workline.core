package workflow.businesstask;

import vrds.model.RepoItem;
import workflow.api.internal.IBusinessTaskAccessor;

public interface IBusinessTaskHandler extends IBusinessTaskAccessor {
    // TODO Split this interface into 2 parts, one that defines methods that are
    // going to be called by the WorkflowTaskProcessor and another that defines methods
    // that are going to be called by the UI

    void initTask(RepoItem businessTask);

    void finishTask(RepoItem businessTask);

}
