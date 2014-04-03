package workline.core.api.internal;

import vrds.model.MetaAttribute;
import vrds.model.RepoItem;
import vrds.model.RepoItemAttribute;
import workline.core.domain.ProcessElementVariableMappingDefinition;

public interface IProcessElementService {
    RepoItemAttribute processVariableMappingForProcessElement(RepoItem businessTask, ProcessElementVariableMappingDefinition variableMappingDefinition, MetaAttribute ... metaAttributes);

    void copyProcessElementVariableValue(RepoItem fromProcessElement, RepoItem toProcessElement, ProcessElementVariableMappingDefinition processVariableMappingDefinition);
}
