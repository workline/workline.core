package workline.core.businesstask.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import vrds.model.meta.TODO;
import vrds.model.meta.TODOTag;
import workline.core.api.internal.IIoVariableSourceDataParser;
import workline.core.domain.EInputVariableScope;
import workline.core.domain.EInputVariableType;
import workline.core.domain.InputVariableTypeData;
import workline.core.domain.MappingDirection;
import workline.core.domain.MappingExpression;
import workline.core.domain.ProcessElementVariableMappingDefinition;
import workline.core.util.CommonUtil;

public class IoVariableSourceDataParser implements IIoVariableSourceDataParser {
    @Inject
    private CommonUtil commonUtil;

    @Override
    @TODO(
            tags = { TODOTag.SPECIFICATION_REQUIRED },
            value = "How should accessRight RepoItems look like? What to do with the parsed data (inputVariableScope, inputVariableSelectionData)?")
    public List<ProcessElementVariableMappingDefinition> parseIoVariableSourceData(Set<String> ioFlatDataSet) {
        if (commonUtil.isNullOrEmpty(ioFlatDataSet)) {
            return Collections.emptyList();
        }

        List<ProcessElementVariableMappingDefinition> processElementVariableMappingDefinitions = new ArrayList<>();

        for (String ioFlatData : ioFlatDataSet) {
            addParsedIoFlatData(processElementVariableMappingDefinitions, ioFlatData);
        }

        return processElementVariableMappingDefinitions;
    }

    private void addParsedIoFlatData(List<ProcessElementVariableMappingDefinition> processElementVariableMappingDefinitions, String ioFlatData) {
        String[] ioFlatDataTokens = ioFlatData.split(";");
        String variableName = ioFlatDataTokens[0];
        String typeDataAsString = ioFlatDataTokens[1];
        String scopeAsString = ioFlatDataTokens[2];
        String inputVariableSelectionQuery = ioFlatDataTokens[3];
        MappingExpression mappingExpression;
        if (ioFlatDataTokens.length > 4) {
            mappingExpression = parseMappingExpression(ioFlatDataTokens[4]);
        } else {
            mappingExpression = null;
        }

        String typeAsString;
        String typeRepoName;
        if (typeDataAsString.contains("::")) {
            String[] typeDataTokens = typeDataAsString.split("::");
            typeAsString = typeDataTokens[0];
            typeRepoName = typeDataTokens[1];
        } else {
            typeAsString = typeDataAsString;
            typeRepoName = null;
        }

        EInputVariableScope inputVariableScope = EInputVariableScope.valueOf(scopeAsString);
        EInputVariableType inputVariableType = EInputVariableType.valueOf(typeAsString);

        ProcessElementVariableMappingDefinition processElementVariableMappingDefinition = new ProcessElementVariableMappingDefinition();

        InputVariableTypeData type = new InputVariableTypeData();
        type.setInputVariableType(inputVariableType);
        type.setRepoName(typeRepoName);

        processElementVariableMappingDefinition.setExpression(variableName);
        processElementVariableMappingDefinition.setType(type);
        processElementVariableMappingDefinition.setInputVariableScope(inputVariableScope);
        processElementVariableMappingDefinition.setInputVariableSelectionQuery(inputVariableSelectionQuery);
        processElementVariableMappingDefinition.setMappingExpression(mappingExpression);

        processElementVariableMappingDefinitions.add(processElementVariableMappingDefinition);
    }

    @TODO(tags = { TODOTag.MISSING_EXCEPTION_HANDLING })
    private MappingExpression parseMappingExpression(String mappingExpressionData) {
        MappingExpression mappingExpression;

        String[] tokens = mappingExpressionData.split(":");

        MappingDirection mappingDirection = MappingDirection.valueOf(tokens[0].toUpperCase());
        String variableExpression = tokens[1];

        mappingExpression = new MappingExpression();
        mappingExpression.setMappingDirection(mappingDirection);
        mappingExpression.setVariableExpression(variableExpression);

        return null;
    }
}
