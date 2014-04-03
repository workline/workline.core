package tmp;

import javax.ejb.Stateless;
import javax.inject.Inject;

import vrds.model.RepoItem;
import workline.core.api.internal.IRepoHandler;
import workline.core.api.internal.IRepoManager;
import workline.core.engine.cache.CacheContainerProvider;

@Stateless
public class TmpService {
    @Inject
    private IRepoHandler repoHandler;
    @Inject
    private IRepoManager repoManager;
    @Inject
    private CacheContainerProvider cacheContainerProvider;

    public void setStringValue(Long repoItemId, Long inheritenceSourceRepoItemId, String attributeName, String stringValue) {
        RepoItem repoItem = repoHandler.getRepoItem(repoItemId);
        RepoItem inheritenceSource = repoHandler.getRepoItem(inheritenceSourceRepoItemId);

        repoHandler.setValue(repoItem, inheritenceSource, attributeName, stringValue);
    }

    public void setStringValue(Long repoItemId, String attributeName, String stringValue) {
        RepoItem repoItem = repoHandler.getRepoItem(repoItemId);
        repoManager.setValue(repoItem, attributeName, stringValue);
    }

    public void putIntoCache(String key, Object value) {
        cacheContainerProvider.getCacheContainer().getCache().put(key, value);
        cacheContainerProvider.getCacheContainer().getCache().put("tmp", value);
        if ("exception".equals(key)) {
            throw new RuntimeException("Throwing exception after putting value into cache.");
        }
    }

    public Object retrieveFromCache(String key) {
        return cacheContainerProvider.getCacheContainer().getCache().get(key);
    }
}
