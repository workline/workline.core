package tmp;

import javax.ejb.Stateless;
import javax.inject.Inject;

import vrds.model.RepoItem;
import workline.core.api.internal.IRepoHandler;
import workline.core.repo.manager.IRepoManager;

@Stateless
public class TmpService {
    @Inject
    private IRepoHandler repoHandler;
    @Inject
    private IRepoManager repoManager;

    public void setStringValue(Long repoItemId, Long benefactorRepoItemId, String attributeName, String stringValue) {
        RepoItem repoItem = repoHandler.getRepoItem(repoItemId);
        RepoItem benefactor = repoHandler.getRepoItem(benefactorRepoItemId);

        repoHandler.setValue(repoItem, benefactor, attributeName, stringValue);
    }

    public void setStringValue(Long repoItemId, String attributeName, String stringValue) {
        RepoItem repoItem = repoHandler.getRepoItem(repoItemId);
        repoManager.setValue(repoItem, attributeName, stringValue);
    }
}
