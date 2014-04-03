package workline.core.repo;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import vrds.model.RepoItem;
import workline.core.api.internal.IRepoListener;
import workline.core.api.internal.IRepoListenerRegistry;
import workline.core.engine.cache.CacheContainerProvider;

public class RepoListenerRegistry implements IRepoListenerRegistry {
    @Inject
    private CacheContainerProvider cacheContainerProvider;

    @Override
    public void register(RepoItem repoItem, IRepoListener repoListener) {
        Long repoItemId = getRepoItemId(repoItem);

        Set<IRepoListener> repoListeners = getRepoItemToRepoListeners().get(repoItemId);

        if (repoListeners == null)
            repoListeners = createNewListenerSet(repoItemId);

        repoListeners.add(repoListener);
    }

    @Override
    public void unRegister(RepoItem repoItem, IRepoListener repoListener) {
        Long repoItemId = getRepoItemId(repoItem);

        Set<IRepoListener> repoListeners = getRepoItemToRepoListeners().get(repoItemId);

        if (repoListeners != null)
            repoListeners.remove(repoListener);
    }

    @Override
    public void executeListeners(RepoItem repoItem) {
        Long repoItemId = getRepoItemId(repoItem);

        Set<IRepoListener> repoListeners = getListeners(repoItemId);

        for (IRepoListener repoListener : repoListeners)
            repoListener.execute(repoItem);
    }

    private Set<IRepoListener> createNewListenerSet(Long repoItemId) {
        Set<IRepoListener> repoListeners;

        repoListeners = new HashSet<>();
        getRepoItemToRepoListeners().put(repoItemId, repoListeners);

        return repoListeners;
    }

    private Set<IRepoListener> getListeners(Long repoItemId) {
        Set<IRepoListener> repoListeners = getRepoItemToRepoListeners().get(repoItemId);

        if (repoListeners == null)
            repoListeners = Collections.emptySet();

        return repoListeners;
    }

    private Map<Long, Set<IRepoListener>> getRepoItemToRepoListeners() {
        return cacheContainerProvider.getCacheContainer().getCache();
    }

    private Long getRepoItemId(RepoItem repoItem) {
        return repoItem.getId();
    }
}
