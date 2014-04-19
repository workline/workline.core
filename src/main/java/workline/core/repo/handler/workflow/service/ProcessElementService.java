package workline.core.repo.handler.workflow.service;

import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import loggee.api.Logged;
import vrds.model.EAttributeType;
import vrds.model.MetaAttribute;
import vrds.model.RepoItem;
import vrds.model.RepoItemAttribute;
import vrds.model.meta.TODO;
import vrds.model.meta.TODOTag;
import workline.core.api.internal.IBusinessTaskHandler;
import workline.core.api.internal.IExpressionProcessor;
import workline.core.api.internal.IProcessElementService;
import workline.core.api.internal.IRepoHandler;
import workline.core.api.internal.IRepoListener;
import workline.core.api.internal.IRepoListenerRegistry;
import workline.core.api.internal.IRepoManager;
import workline.core.businesstask.util.VariableExpressionParser;
import workline.core.domain.EInputVariableType;
import workline.core.domain.InputVariableTypeData;
import workline.core.domain.MappingDirection;
import workline.core.domain.MappingExpression;
import workline.core.domain.ProcessElementVariableMappingDefinition;
import workline.core.engine.constants.WorklineEngineConstants;
import workline.core.meta.NeededToHandleInRepoRegistry;
import workline.core.meta.NotNeededToHandleInRepoRegistry;
import workline.core.repo.listener.MapperListener;
import workline.core.repo.listener.RepoItemValueReference;

@Logged
@ApplicationScoped
public class ProcessElementService implements IProcessElementService {
    @Inject
    private IRepoHandler repoHandler;
    @Inject
    private IRepoManager repoManager;
    @Inject
    private IBusinessTaskHandler businessTaskHandler;
    @Inject
    private IExpressionProcessor expressionProcessor;
    @Inject
    private IRepoListenerRegistry repoListenerRegistry;

    @Override
    public RepoItemAttribute processVariableMappingForProcessElement(RepoItem processElement,
            ProcessElementVariableMappingDefinition variableMappingDefinition,
            MetaAttribute ... metaAttributes) {

        RepoItemAttribute variableStoringAttribute;

        variableStoringAttribute = createVariableStoringAttribute(processElement, variableMappingDefinition, metaAttributes);

        processMapping(processElement, variableMappingDefinition);

        return variableStoringAttribute;
    }

    private void processMapping(RepoItem processElement, ProcessElementVariableMappingDefinition variableMappingDefinition) {
        MappingExpression mappingExpression = variableMappingDefinition.getMappingExpression();

        if (mappingExpression != null) {
            MappingDirection mappingDirection = mappingExpression.getMappingDirection();
            Long processElementId = processElement.getId();

            if (mappingDirection == MappingDirection.TO) { // TO Logic
                String mappedFromExpression = variableMappingDefinition.getExpression();
                String mappedToExpression = variableMappingDefinition.getMappingExpression().getVariableExpression();

                RepoItemValueReference toRepoItemValueReference = expressionProcessor.getRepoItemValueReference(processElementId, mappedToExpression);
                RepoItemValueReference fromRepoItemValueReference = expressionProcessor.getRepoItemValueReference(processElementId, mappedFromExpression);

                // TODO HERE
                // repoManager.setValue(processElement, attributeName, value);

                // cucc
            } else if (mappingDirection == MappingDirection.FROM) {
                String mappedToExpression = variableMappingDefinition.getMappingExpression().getVariableExpression();
                String mappedFromExpression = variableMappingDefinition.getExpression();

                IRepoListener repoListener = new MapperListener(repoManager, businessTaskHandler, expressionProcessor, processElementId, mappedFromExpression,
                        mappedToExpression);

                RepoItem repoItem = expressionProcessor.getRepoItemValueReference(processElementId, mappedToExpression).getRepoItem();

                repoListenerRegistry.register(repoItem, repoListener);
            }
        }
    }

    private RepoItemAttribute createVariableStoringAttribute(RepoItem processElement, ProcessElementVariableMappingDefinition variableMappingDefinition,
            MetaAttribute ... metaAttributes) {

        String variableExpression = variableMappingDefinition.getExpression();

        RepoItemAttribute variableStoringAttribute;
        List<String> tokens = VariableExpressionParser.getInstance().getTokens(variableExpression);
        if (tokens.size() == 1 && processElement.getAttribute(variableExpression) == null) {
            variableStoringAttribute = repoHandler.addAttribute(processElement, variableExpression, variableMappingDefinition.getType()
                    .getInputVariableType().getAttributeType());

            addMetaAttributes(variableStoringAttribute, variableMappingDefinition, metaAttributes);
            addNewValueIfNeeded(variableStoringAttribute, variableMappingDefinition);
        } else {
            variableStoringAttribute = null;
        }

        return variableStoringAttribute;
    }

    @NeededToHandleInRepoRegistry
    @Override
    public void copyProcessElementVariableValue(RepoItem fromProcessElement, RepoItem toProcessElement,
            ProcessElementVariableMappingDefinition processVariableMappingDefinition) {
        String variableName = processVariableMappingDefinition.getExpression();

        Object value = repoHandler.getNonInheritingValue(fromProcessElement, variableName, processVariableMappingDefinition.getType().getInputVariableType()
                .getAttributeType().getAttributeValueHandler());

        toProcessElement.setValue(variableName, value);
    }

    private void addMetaAttributes(RepoItemAttribute ioAttribute, ProcessElementVariableMappingDefinition variableMappingDefinition,
            MetaAttribute ... metaAttributes) {
        addMetaAttributes(ioAttribute, metaAttributes);
        addVariableTagMetaAttribute(ioAttribute);
        addParsedDataAsMetaAttributes(variableMappingDefinition, ioAttribute);
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

    @TODO(tags = { TODOTag.BUG }, value = "The mappedToExpression doesn't seem to be working.")
    private void addParsedDataAsMetaAttributes(ProcessElementVariableMappingDefinition variableMappingDefinition, RepoItemAttribute ioAttribute) {
        InputVariableTypeData type = variableMappingDefinition.getType();

        EInputVariableType inputVariableType = type.getInputVariableType();
        repoHandler.createMetaAttribute(ioAttribute, WorklineEngineConstants.VARIABLE_TYPE, EAttributeType.STRING, inputVariableType.toString());

        String typeRepoName = type.getRepoName();
        repoHandler.createMetaAttribute(ioAttribute, WorklineEngineConstants.VARIABLE_TYPE_REPO_NAME, EAttributeType.STRING, typeRepoName);

        String inputVariableSelectionQuery = variableMappingDefinition.getInputVariableSelectionQuery();
        repoHandler.createMetaAttribute(ioAttribute, WorklineEngineConstants.VARIABLE_SELECTION_QUERY, EAttributeType.STRING, inputVariableSelectionQuery);

        // TODO Do we need mapping metadata? (Mapped to / Mapped from expression)
    }

    @NotNeededToHandleInRepoRegistry
    private void addNewValueIfNeeded(RepoItemAttribute ioAttribute, ProcessElementVariableMappingDefinition variableMappingDefinition) {
        String inputVariableSelectionQuery = variableMappingDefinition.getInputVariableSelectionQuery();
        if (WorklineEngineConstants.NEW_VARIABLE_SELECTION_QUERY_VALUE.equals(inputVariableSelectionQuery) && ioAttribute.getValue() == null) {
            RepoItem newValue = new RepoItem();
            // FIXME Add attributes to 'new' value on the fly when attribute with mappedToExpression is set
            repoHandler.persistRepoItem(newValue);
            ioAttribute.setValue(newValue);
        }
    }
}
