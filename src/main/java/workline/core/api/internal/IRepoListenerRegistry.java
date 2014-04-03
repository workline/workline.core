package workline.core.api.internal;

import vrds.model.RepoItem;

public interface IRepoListenerRegistry {
    void register(RepoItem repoItem, IRepoListener repoListener);

    void unRegister(RepoItem repoItem, IRepoListener repoListener);

    void executeListeners(RepoItem repoItem);
}
