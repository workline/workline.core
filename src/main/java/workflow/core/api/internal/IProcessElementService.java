package workflow.core.api.internal;

import vrds.model.MetaAttribute;
import vrds.model.RepoItem;
import vrds.model.RepoItemAttribute;
import workflow.core.domain.ProcessElementVariableDefinition;

public interface IProcessElementService {
    RepoItemAttribute addVariableToProcessElement(RepoItem businessTask, ProcessElementVariableDefinition variableDefinition, MetaAttribute ... metaAttributes);

    void copyProcessElementVariable(RepoItem fromProcessElement, RepoItem toProcessElement, ProcessElementVariableDefinition processVariableDefinition);
}
