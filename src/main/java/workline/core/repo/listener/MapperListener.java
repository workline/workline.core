package workline.core.repo.listener;

import java.util.Iterator;
import java.util.List;

import vrds.model.RepoItem;
import vrds.model.attributetype.RepoItemAttributeValueHandler;
import workline.core.api.internal.IBusinessTaskHandler;
import workline.core.api.internal.IExpressionProcessor;
import workline.core.api.internal.IRepoListener;
import workline.core.api.internal.IRepoManager;
import workline.core.businesstask.util.VariableExpressionParser;

public class MapperListener implements IRepoListener {
    private IRepoManager repoManager;
    private IBusinessTaskHandler businessTaskHandler;
    private IExpressionProcessor expressionProcessor;

    private Long processElementId;

    private List<String> mappedFromTokens;
    private String mappedToExpression;

    public MapperListener(IRepoManager repoManager, IBusinessTaskHandler businessTaskHandler, IExpressionProcessor expressionProcessor, Long processElementId,
            String mappedFromExpression, String mappedToExpression) {

        this.repoManager = repoManager;
        this.businessTaskHandler = businessTaskHandler;
        this.expressionProcessor = expressionProcessor;
        this.processElementId = processElementId;

        mappedFromTokens = VariableExpressionParser.getInstance().getTokens(mappedFromExpression);
        this.mappedToExpression = mappedToExpression;
    }

    @Override
    public void execute(RepoItem repoItem) {
        Object value = getValue();

        editVariable(value);
    }

    private Object getValue() {
        Object value = getFromExpressionItem();

        return value;
    }

    private void editVariable(Object value) {
        RepoItemValueReference expressedItem = getToExpressionReference();

        RepoItem modifiedRepoItem = expressedItem.getRepoItem();
        String mappedToAttributeName = expressedItem.getAttributeName();

        repoManager.setValue(modifiedRepoItem, mappedToAttributeName, value);
    }

    private Object getFromExpressionItem() {
        return getExpressedItem(mappedFromTokens);
    }

    private RepoItemValueReference getToExpressionReference() {
        RepoItemValueReference repoItemValueReference = expressionProcessor.getRepoItemValueReference(processElementId, mappedToExpression);

        return repoItemValueReference;
    }

    private Object getExpressedItem(List<String> tokens) {
        Object expressedItem = null;

        String variableName = tokens.remove(0);

        RepoItem variable = businessTaskHandler.readVariable(processElementId, variableName, RepoItemAttributeValueHandler.getInstance());

        Iterator<String> mappedFromTokensIterator = tokens.iterator();
        while(mappedFromTokensIterator.hasNext()) {
            String attributeName = mappedFromTokensIterator.next();

            expressedItem = variable.getValue(attributeName);

            if (expressedItem instanceof RepoItem) {
                variable = (RepoItem) expressedItem;
            }
        }

        return expressedItem;
    }
}
