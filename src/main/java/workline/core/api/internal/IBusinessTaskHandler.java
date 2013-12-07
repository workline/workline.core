package workline.core.api.internal;

import java.util.List;

import vrds.model.RepoItem;
import workline.core.domain.ProcessElementVariableDefinition;

public interface IBusinessTaskHandler extends IBusinessTaskAccessor {
    // TODO Split this interface into 2 parts, one that defines methods that are
    // going to be called by the WorkflowTaskProcessor and another that defines methods
    // that are going to be called by the UI

    void initTask(RepoItem process, RepoItem businessTask);

    void finishTask(RepoItem businessTask);

    List<ProcessElementVariableDefinition> parseIoVariableSourceData(String ioFlatData);
}
