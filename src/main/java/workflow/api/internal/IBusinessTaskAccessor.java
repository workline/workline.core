package workflow.api.internal;

import vrds.model.RepoItem;

public interface IBusinessTaskAccessor {
    void readVariable(RepoItem businessTask, String variableName);

    void writeVariable(RepoItem businessTask, String variableName, Object value);
}
