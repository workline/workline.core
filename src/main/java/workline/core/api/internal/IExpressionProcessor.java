package workline.core.api.internal;

import workline.core.repo.listener.RepoItemValueReference;

public interface IExpressionProcessor {
    RepoItemValueReference getRepoItemValueReference(Long processElementId, String expression);
}
