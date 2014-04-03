package workline.core.businesstask;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import loggee.api.Logged;
import vrds.model.EAttributeType;
import vrds.model.IValueWrapper;
import vrds.model.MetaAttribute;
import vrds.model.RepoItem;
import vrds.model.RepoItemAttribute;
import vrds.model.attributetype.AttributeValueHandler;
import vrds.model.attributetype.RepoItemAttributeValueHandler;
import vrds.model.attributetype.StringAttributeValueHandler;
import vrds.model.meta.TODO;
import vrds.model.meta.TODOTag;
import workline.core.api.internal.IBusinessTaskHandler;
import workline.core.api.internal.IIoVariableSourceDataParser;
import workline.core.api.internal.IProcessElementService;
import workline.core.api.internal.IRepoHandler;
import workline.core.api.internal.IRepoManager;
import workline.core.domain.EInputVariableScope;
import workline.core.domain.InputBehaviourLogicURI;
import workline.core.domain.MappedToData;
import workline.core.domain.ProcessElementVariableMappingDefinition;
import workline.core.engine.constants.WorklineEngineConstants;

@Logged
public class DefaultBusinessTaskHandler implements IBusinessTaskHandler {
    @Inject
    private IRepoManager repoManager;
    @Inject
    private IRepoHandler repoHandler;
    @Inject
    private IProcessElementService processElementService;
    @Inject
    private IIoVariableSourceDataParser ioVariableSourceDataParser;

    @Override
    public void initBusinessTask(RepoItem process, RepoItem businessTask) {
        preProcess(businessTask);
        initialIO(process, businessTask);
        // TODO LATER Notify actors
    }

    @Override
    public Object readVariable(Long businessTaskId, String variableName) {
        RepoItem businessTask = repoHandler.getRepoItem(businessTaskId);

        return businessTask.getValue(variableName);
    }

    @Override
    public <T, W extends IValueWrapper<T>> T readVariable(Long businessTaskId, String variableName, AttributeValueHandler<T, W> attributeValueHandler) {
        RepoItem businessTask = repoHandler.getRepoItem(businessTaskId);

        return businessTask.getValue(variableName, attributeValueHandler);
    }

    @Override
    public void writeVariable(Long businessTaskId, String variableName, Long repoItemId) {
        RepoItem value = repoHandler.getRepoItem(repoItemId);

        writeVariable(businessTaskId, variableName, value);
    }

    @Override
    public void writeVariable(Long businessTaskId, String variableName, Object value) {
        RepoItem businessTask = repoHandler.getRepoItem(businessTaskId);

        repoManager.setValue(businessTask, variableName, value);

        postProcessVariableEditing(businessTask, variableName, value);
    }

    @Override
    public void modifyVariable(Long businessTaskId, String variableName, String attributeName, Long repoItemId) {
        RepoItem value = repoHandler.getRepoItem(repoItemId);

        modifyVariable(businessTaskId, variableName, attributeName, value);
    }

    @TODO(
            tags = { TODOTag.INHERITENCE, TODOTag.SPECIFICATION_REQUIRED },
            value = "Is the attribute of the RepoItem stored as a variable alway a non-inheriting one?")
    @Override
    public void modifyVariable(Long businessTaskId, String variableName, String attributeName, Object value) {
        RepoItem businessTask = repoHandler.getRepoItem(businessTaskId);

        // TODO Is it always non-inheriting?
        RepoItem variableRepoItem = repoHandler.getNonInheritingValue(businessTask, variableName, RepoItemAttributeValueHandler.getInstance());
        repoManager.setValue(variableRepoItem, attributeName, value);

        postProcessVariableEditing(businessTask, variableName, value);
    }

    @Override
    public void finishTask(RepoItem businessTask) {
        postProcess(businessTask);
        close(businessTask);
    }

    private void postProcessVariableEditing(RepoItem businessTask, String variableName, Object value) {
        setMappedToValuesIfNeeded(businessTask, variableName, value);

        runInputBehaviourLogic(businessTask);

        setContextDependentIO(businessTask);
    }

    private void setMappedToValuesIfNeeded(RepoItem businessTask, String variableName, Object value) {
        RepoItemAttribute attributeStoringVariable = businessTask.getAttribute(variableName);
        String mappedToExpression = attributeStoringVariable.getMetaAttributeValue(WorklineEngineConstants.VARIABLE_MAPPED_TO_EXPRESSION,
                StringAttributeValueHandler.getInstance());

        if (mappedToExpression != null) {
            MappedToData mappedToData = parseMappedToExpression(mappedToExpression);

            RepoItem mappedToVariable = repoHandler.getNonInheritingValue(businessTask, mappedToData.getVariableName(),
                    RepoItemAttributeValueHandler.getInstance());
            String variableNameOfVariable = mappedToData.getVariableNameOfVariable();

            // TODO Need to make sure that postProcessVariableEditing is called recursively
            if (mappedToVariable.getAttribute(variableNameOfVariable) == null) {
                repoHandler.addAttribute(mappedToVariable, variableNameOfVariable, attributeStoringVariable.getType(), value);
            } else {
                repoManager.setValue(mappedToVariable, variableNameOfVariable, value);
            }
        }

    }

    private MappedToData parseMappedToExpression(String mappedToExpression) {
        MappedToData mappedToData = new MappedToData();

        String[] mappedToExpressionTokens = mappedToExpression.split("\\.");

        String variableName = mappedToExpressionTokens[0];
        String variableNameOfVariable = mappedToExpressionTokens[1];

        mappedToData.setVariableName(variableName);
        mappedToData.setVariableNameOfVariable(variableNameOfVariable);

        return mappedToData;
    }

    private void preProcess(RepoItem businessTask) {
        // TODO LATER Implement
    }

    private void initialIO(RepoItem process, RepoItem businessTask) {
        RepoItem businessTaskDefinition = getBusinessTaskDefinition(businessTask);

        String taskSpecificProcessVariableMappingDefinitionData = repoHandler.getNonInheritingValue(businessTaskDefinition,
                WorklineEngineConstants.TASK_SPECIFIC_PROCESS_VARIABLES_DEFINITION, StringAttributeValueHandler.getInstance());
        List<ProcessElementVariableMappingDefinition> taskSpecificProcessVariableMappingDefinitionList = ioVariableSourceDataParser
                .parseIoVariableSourceData(Collections.singleton(taskSpecificProcessVariableMappingDefinitionData));

        for (ProcessElementVariableMappingDefinition taskSpecificProcessVariableMappingDefinition : taskSpecificProcessVariableMappingDefinitionList) {
            processElementService.processVariableMappingForProcessElement(businessTask, taskSpecificProcessVariableMappingDefinition);

            if (taskSpecificProcessVariableMappingDefinition.getInputVariableScope() == EInputVariableScope.PROCESS) {
                processElementService.copyProcessElementVariableValue(process, businessTask, taskSpecificProcessVariableMappingDefinition);
            }
        }

        setContextDependentIO(process, businessTask);
    }

    private void postProcess(RepoItem businessTask) {
        // FIXME LATER Auto-generated method stub
    }

    private void close(RepoItem businessTask) {
        // TODO LATER Auto-generated method stub

    }

    private void callInputBehaviourService(Long taskId, InputBehaviourLogicURI inputBehaviourLogicURI) {
        // FIXME LATER Auto-generated method stub

    }

    private void setContextDependentIO(RepoItem businessTask) {
        RepoItem process = repoHandler.getNonInheritingValue(businessTask, WorklineEngineConstants.PROCESS, RepoItemAttributeValueHandler.getInstance());

        setContextDependentIO(process, businessTask);
    }

    @TODO(tags = { TODOTag.OPTIMIZATION_TARGET_CANDIDATE })
    private void setContextDependentIO(RepoItem process, RepoItem businessTask) {
        RepoItem businessTaskDefinition = getBusinessTaskDefinition(businessTask);

        String ioVariableSourceExpression = repoHandler.getNonInheritingValue(businessTaskDefinition, WorklineEngineConstants.IO_VARIABLE_SOURCE,
                StringAttributeValueHandler.getInstance());
        // TODO It would be great if this parsed object list could be stored somewhere instead of being generated every time
        List<ProcessElementVariableMappingDefinition> contextDependentProcessVariableMappingDefinitionList = getIoVariableSourceByExpression(businessTask,
                ioVariableSourceExpression);

        // TODO LATER Remove previous, not used variables
        for (ProcessElementVariableMappingDefinition contextDependentProcessVariableMappingDefinition : contextDependentProcessVariableMappingDefinitionList) {
            MetaAttribute dynamicTag = repoHandler.createMetaAttribute(WorklineEngineConstants.DYNAMIC, EAttributeType.STRING, ioVariableSourceExpression);

            RepoItemAttribute variable = processElementService.processVariableMappingForProcessElement(businessTask, contextDependentProcessVariableMappingDefinition,
                    dynamicTag);

            if (variable != null) {
                if (contextDependentProcessVariableMappingDefinition.getInputVariableScope() == EInputVariableScope.PROCESS) {
                    if (process.hasAttribute(contextDependentProcessVariableMappingDefinition.getExpression())) {
                        processElementService.copyProcessElementVariableValue(process, businessTask, contextDependentProcessVariableMappingDefinition);
                    } else {
                        processElementService.processVariableMappingForProcessElement(process, contextDependentProcessVariableMappingDefinition, dynamicTag);
                    }
                } else {
                    // Do nothing. All variables (regardless of scope) has already been added to business task.
                }
            }
        }
    }

    private List<ProcessElementVariableMappingDefinition> getIoVariableSourceByExpression(RepoItem businessTask, String expression) {
        Set<String> flatDataSet = getFlatDataSetByExpression(businessTask, expression);

        return ioVariableSourceDataParser.parseIoVariableSourceData(flatDataSet);
    }

    private RepoItem getBusinessTaskDefinition(RepoItem businessTask) {
        RepoItem businessTaskDefinition = repoHandler.getNonInheritingValue(businessTask, WorklineEngineConstants.BUSINESS_TASK_DEFINITION,
                RepoItemAttributeValueHandler.getInstance());

        return businessTaskDefinition;
    }

    private void runInputBehaviourLogic(RepoItem businessTask) {
        RepoItem businessTaskDefinition = getBusinessTaskDefinition(businessTask);
        String inputBehaviourSourceExpression = repoHandler.getNonInheritingValue(businessTaskDefinition, WorklineEngineConstants.INPUT_BEHAVIOUR_SOURCE,
                StringAttributeValueHandler.getInstance());

        InputBehaviourLogicURI inputBehaviourLogicURI = getInputBehaviourLogicURIyExpression(businessTask, inputBehaviourSourceExpression);

        callInputBehaviourService(businessTask.getId(), inputBehaviourLogicURI);
    }

    private InputBehaviourLogicURI getInputBehaviourLogicURIyExpression(RepoItem businessTask, String expression) {
        Set<String> flatDataSet = getFlatDataSetByExpression(businessTask, expression);

        InputBehaviourLogicURI inputBehaviourLogicURI = parseInputBehaviourLogicURI(flatDataSet);

        return inputBehaviourLogicURI;
    }

    private InputBehaviourLogicURI parseInputBehaviourLogicURI(Set<String> flatDataSet) {
        // FIXME LATER Auto-generated method stub
        return null;
    }

    @TODO(tags = { TODOTag.MISSING_IMPLEMENTATION, TODOTag.INHERITENCE }, value = "getValue() inheritence")
    private Set<String> getFlatDataSetByExpression(RepoItem businessTask, String expression) {
        Set<String> flatDataSet = new HashSet<>();

        String[] expressionTokens = expression.split("\\.");
        String attributeNameReferencingRepoItem = expressionTokens[0];
        String attributeNameOfReferencedRepoItem = expressionTokens[1];

        RepoItem repoItemContainingFlatData = repoHandler.getNonInheritingValue(businessTask, attributeNameReferencingRepoItem,
                RepoItemAttributeValueHandler.getInstance());

        if (repoItemContainingFlatData == null) {
            flatDataSet = Collections.emptySet();
        } else {
            Set<String> flatDataTableSet = repoHandler.getInheritingValues(repoItemContainingFlatData, attributeNameOfReferencedRepoItem,
                    StringAttributeValueHandler.getInstance());

            for (String flatDataTableAsString : flatDataTableSet) {
                String[] flatDataRecords = flatDataTableAsString.split("\n");
                flatDataSet.addAll(Arrays.asList(flatDataRecords));
            }
        }

        return flatDataSet;
    }

}
