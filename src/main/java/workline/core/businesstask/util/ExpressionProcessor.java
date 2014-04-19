package workline.core.businesstask.util;

import java.util.Iterator;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import vrds.model.RepoItem;
import workline.core.api.internal.IBusinessTaskHandler;
import workline.core.api.internal.IExpressionProcessor;
import workline.core.repo.listener.RepoItemValueReference;

@ApplicationScoped
public class ExpressionProcessor implements IExpressionProcessor {
    @Inject
    private IBusinessTaskHandler businessTaskHandler;

    @Override
    public RepoItemValueReference getRepoItemValueReference(Long processElementId, String expression) {
        RepoItemValueReference repoItemValueReference = getExpressedReference(processElementId, VariableExpressionParser.getInstance().getTokens(expression));

        return repoItemValueReference;
    }

    private RepoItemValueReference getExpressedReference(Long processElementId, List<String> tokens) {
        RepoItemValueReference repoItemValueReference;

        String variableName = tokens.remove(0);

        Object variableValue = businessTaskHandler.readVariable(processElementId, variableName);

        RepoItem repoItem;
        String attributeName;
        if (variableValue instanceof RepoItem) {
            repoItem = (RepoItem) variableValue;

            Object value = null;
            attributeName = null;

            Iterator<String> mappedFromTokensIterator = tokens.iterator();
            while(mappedFromTokensIterator.hasNext()) {
                if (value instanceof RepoItem) {
                    repoItem = (RepoItem) value;
                }

                attributeName = mappedFromTokensIterator.next();

                value = repoItem.getValue(attributeName);
            }
        } else {
            repoItem = null;
            attributeName = null;
        }

        repoItemValueReference = new RepoItemValueReference(repoItem, attributeName);

        return repoItemValueReference;
    }

    protected void setBusinessTaskHandler(IBusinessTaskHandler businessTaskHandler) {
        this.businessTaskHandler = businessTaskHandler;
    }
}
