package workline.core.api.internal;

import vrds.model.RepoItem;

public interface IConsistencyChecker {
    boolean checkConsistency(String consistencyCheckerId, RepoItem repoItem);
}
