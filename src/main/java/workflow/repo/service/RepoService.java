package workflow.repo.service;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import loggee.api.Logged;
import vrds.model.RepoItem;
import workflow.util.Primary;

@Stateless
@Logged
public class RepoService {
    @Inject
    @Primary
    private EntityManager entityManager;

    public RepoItem getRepo(Long repoId) {
        RepoItem repo;

        repo = entityManager.find(RepoItem.class, repoId);

        return repo;
    }

    public Object getRepoAttributeValue(Long repoId, String repoAttributeName) {
        Object repoAttributeValue;

        RepoItem repo = getRepo(repoId);
        repoAttributeValue = repo.getValue(repoAttributeName);

        return repoAttributeValue;
    }
}
