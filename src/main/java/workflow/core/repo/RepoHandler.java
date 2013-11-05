package workflow.core.repo;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import vrds.model.EAttributeType;
import vrds.model.RepoItem;
import vrds.model.RepoItemAttribute;
import workflow.core.util.Primary;

@Stateless
public class RepoHandler implements IRepoHandler {
    @Inject
    @Primary
    private EntityManager entityManager;

    @Override
    public RepoItem getRepoItem(Long repoItemId) {
        return entityManager.find(RepoItem.class, repoItemId);
    }

    @Override
    public void persistRepoItem(RepoItem repoItem) {
        _persist(repoItem);
    }

    @Override
    public void persistRepoItemAttribute(RepoItemAttribute repoItemAttribute) {
        _persist(repoItemAttribute);
    }

    @Override
    public void addAttribute(RepoItem repoItem, String attributeName, EAttributeType type) {
        _addAttribute(repoItem, attributeName, type);
    }

    @Override
    public void addAttribute(RepoItem repoItem, String attributeName, EAttributeType type, Object value) {
        RepoItemAttribute repoItemAttribute = _addAttribute(repoItem, attributeName, type);

        repoItemAttribute.setValue(value);
    }

    private RepoItemAttribute _addAttribute(RepoItem repoItem, String attributeName, EAttributeType type) {
        RepoItemAttribute repoItemAttribute = new RepoItemAttribute();
        repoItemAttribute.setNameAndType(attributeName, type);
        repoItemAttribute.setRepoItem(repoItem);

        _persist(repoItemAttribute);

        return repoItemAttribute;
    }

    private void _persist(Object repoItemAttribute) {
        entityManager.persist(repoItemAttribute);
    }
}
