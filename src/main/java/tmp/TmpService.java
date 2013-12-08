package tmp;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import vrds.model.RepoItem;
import workline.core.api.internal.IRepoHandler;
import workline.core.util.Primary;

@Stateless
public class TmpService {
    @Inject
    private IRepoHandler repoHandler;
    @Inject
    @Primary
    private EntityManager entityManager;

    public Object setStringValue(Long repoItemId, Long benefactorRepoItemId, String attributeName, String stringValue) {
        RepoItem repoItem = repoHandler.getRepoItem(repoItemId);
        RepoItem benefactor = repoHandler.getRepoItem(benefactorRepoItemId);

        repoHandler.setValue(repoItem, benefactor, attributeName, stringValue);

        return true;
    }
}
