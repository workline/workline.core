package workline.core.repo.workflow.service;

import java.util.Arrays;

import javax.inject.Inject;

import loggee.api.Logged;
import vrds.model.EAttributeType;
import vrds.model.MetaAttribute;
import vrds.model.RepoItem;
import vrds.model.RepoItemAttribute;
import workline.core.api.internal.IProcessElementService;
import workline.core.api.internal.IRepoHandler;
import workline.core.domain.EInputVariableType;
import workline.core.domain.InputVariableTypeData;
import workline.core.domain.ProcessElementVariableDefinition;
import workline.core.engine.constants.WorklineEngineConstants;

@Logged
public class ProcessElementService implements IProcessElementService {
    @Inject
    private IRepoHandler repoHandler;

    @Override
    public RepoItemAttribute addVariableToProcessElement(RepoItem businessTask, ProcessElementVariableDefinition variableDefinition,
            MetaAttribute ... metaAttributes) {

        RepoItemAttribute ioAttribute = repoHandler.addAttribute(businessTask, variableDefinition.getName(), variableDefinition.getType()
                .getInputVariableType().getAttributeType());

        addMetaAttributesAndAddNewValueIfNeeded(ioAttribute, variableDefinition, metaAttributes);

        return ioAttribute;
    }

    @Override
    public void copyProcessElementVariable(RepoItem fromProcessElement, RepoItem toProcessElement, ProcessElementVariableDefinition processVariableDefinition) {
        String variableName = processVariableDefinition.getName();

        Object value = fromProcessElement.getValue(variableName, processVariableDefinition.getType().getInputVariableType().getAttributeType()
                .getAttributeValueHandler());

        toProcessElement.setValue(variableName, value);
    }

    private void addMetaAttributesAndAddNewValueIfNeeded(RepoItemAttribute ioAttribute, ProcessElementVariableDefinition variableDefinition,
            MetaAttribute ... metaAttributes) {
        addMetaAttributes(ioAttribute, metaAttributes);
        addVariableTagMetaAttribute(ioAttribute);
        addParsedDataAsMetaAttributesAndAddNewValueIfNeeded(variableDefinition, ioAttribute);
    }

    private void addMetaAttributes(RepoItemAttribute attribute, MetaAttribute ... metaAttributes) {
        if (metaAttributes != null) {
            for (MetaAttribute metaAttribute : metaAttributes) {
                metaAttribute.setOwnerAttribute(attribute);
            }
        }
        attribute.getMetaAttributes().addAll(Arrays.asList(metaAttributes));
    }

    private void addVariableTagMetaAttribute(RepoItemAttribute ioAttribute) {
        repoHandler.createMetaAttribute(ioAttribute, WorklineEngineConstants.VARIABLE, EAttributeType.STRING);
    }

    private void addParsedDataAsMetaAttributesAndAddNewValueIfNeeded(ProcessElementVariableDefinition variableDefinition, RepoItemAttribute ioAttribute) {
        InputVariableTypeData type = variableDefinition.getType();

        EInputVariableType inputVariableType = type.getInputVariableType();
        repoHandler.createMetaAttribute(ioAttribute, WorklineEngineConstants.VARIABLE_TYPE, EAttributeType.STRING, inputVariableType.toString());

        String typeRepoName = type.getRepoName();
        repoHandler.createMetaAttribute(ioAttribute, WorklineEngineConstants.VARIABLE_TYPE_REPO_NAME, EAttributeType.STRING, typeRepoName);

        String inputVariableSelectionQuery = variableDefinition.getInputVariableSelectionQuery();
        repoHandler.createMetaAttribute(ioAttribute, WorklineEngineConstants.VARIABLE_SELECTION_QUERY, EAttributeType.STRING, inputVariableSelectionQuery);

        String mappedToExpression = variableDefinition.getMappedToExpression();
        repoHandler.createMetaAttribute(ioAttribute, WorklineEngineConstants.VARIABLE_MAPPED_TO_EXPRESSION, EAttributeType.STRING, mappedToExpression);

        addNewValueIfNeeded(ioAttribute, variableDefinition);
    }

    private void addNewValueIfNeeded(RepoItemAttribute ioAttribute, ProcessElementVariableDefinition variableDefinition) {
        String inputVariableSelectionQuery = variableDefinition.getInputVariableSelectionQuery();
        if (WorklineEngineConstants.NEW_VARIABLE_SELECTION_QUERY_VALUE.equals(inputVariableSelectionQuery)) {
            RepoItem newValue = new RepoItem();
            // FIXME Add proper attributes to newValue
            repoHandler.persistRepoItem(newValue);
            ioAttribute.setValue(newValue);
        }
    }
}
