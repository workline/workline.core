package workflow.core.repo.workflow.service;

import javax.inject.Inject;

import vrds.model.RepoItem;
import vrds.model.RepoItemAttribute;
import workflow.core.domain.ProcessElementVariableDefinition;
import workflow.core.repo.IRepoHandler;

public class ProcessElementService implements IProcessElementService {
    @Inject
    private IRepoHandler repoHandler;

    @Override
    public RepoItemAttribute addVariableToProcessElement(RepoItem businessTask, ProcessElementVariableDefinition variableDefinition) {
        RepoItemAttribute ioAttribute = new RepoItemAttribute();
        ioAttribute.setRepoItem(businessTask);
        ioAttribute.setNameAndType(variableDefinition.getName(), variableDefinition.getType().getInputVariableType().getAttributeType());

        repoHandler.persistRepoItemAttribute(ioAttribute);

        return ioAttribute;
    }

    @Override
    public void copyProcessElementVariable(RepoItem fromProcessElement, RepoItem toProcessElement, ProcessElementVariableDefinition processVariableDefinition) {
        String variableName = processVariableDefinition.getName();

        Object value = fromProcessElement.getValue(variableName, processVariableDefinition.getType().getInputVariableType().getAttributeType()
                .getAttributeValueHandler());

        toProcessElement.setValue(variableName, value);
    }
}
