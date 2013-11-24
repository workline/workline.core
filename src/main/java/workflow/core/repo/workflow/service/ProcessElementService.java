package workflow.core.repo.workflow.service;

import javax.inject.Inject;

import loggee.api.Logged;
import vrds.model.MetaAttribute;
import vrds.model.RepoItem;
import vrds.model.RepoItemAttribute;
import workflow.core.api.internal.IProcessElementService;
import workflow.core.api.internal.IRepoHandler;
import workflow.core.domain.ProcessElementVariableDefinition;

@Logged
public class ProcessElementService implements IProcessElementService {
    @Inject
    private IRepoHandler repoHandler;

    @Override
    public RepoItemAttribute addVariableToProcessElement(RepoItem businessTask, ProcessElementVariableDefinition variableDefinition,
            MetaAttribute ... metaAttributes) {
        RepoItemAttribute ioAttribute = new RepoItemAttribute();
        ioAttribute.setRepoItem(businessTask);
        ioAttribute.setNameAndType(variableDefinition.getName(), variableDefinition.getType().getInputVariableType().getAttributeType());

        addMetaAttributes(ioAttribute, metaAttributes);

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

    private void addMetaAttributes(RepoItemAttribute attribute, MetaAttribute ... metaAttributes) {
        if (metaAttributes != null) {
            for (MetaAttribute metaAttribute : metaAttributes) {
                metaAttribute.setOwnerAttribute(attribute);
            }
        }
    }
}
