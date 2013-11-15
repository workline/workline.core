package workflow.core.repo.workflow.service;

import vrds.model.RepoItem;
import vrds.model.RepoItemAttribute;
import workflow.core.domain.ProcessElementVariableDefinition;

public interface IProcessElementService {
    RepoItemAttribute addVariableToProcessElement(RepoItem businessTask, ProcessElementVariableDefinition variableDefinition);

    void copyProcessElementVariable(RepoItem fromProcessElement, RepoItem toProcessElement, ProcessElementVariableDefinition processVariableDefinition);
}
