package workline.core.api.internal;

import vrds.model.MetaAttribute;
import vrds.model.RepoItem;
import vrds.model.RepoItemAttribute;
import workline.core.domain.ProcessElementVariableDefinition;

public interface IProcessElementService {
    RepoItemAttribute addVariableToProcessElement(RepoItem businessTask, ProcessElementVariableDefinition variableDefinition, MetaAttribute ... metaAttributes);

    void copyProcessElementVariableValue(RepoItem fromProcessElement, RepoItem toProcessElement, ProcessElementVariableDefinition processVariableDefinition);
}
