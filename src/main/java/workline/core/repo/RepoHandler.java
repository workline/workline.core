package workline.core.repo;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import loggee.api.Logged;
import vrds.model.EAttributeType;
import vrds.model.MetaAttribute;
import vrds.model.RepoItem;
import vrds.model.RepoItemAttribute;
import workline.core.api.internal.IRepoHandler;
import workline.core.util.Primary;

@Logged
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
    public RepoItemAttribute addAttribute(RepoItem repoItem, String attributeName, EAttributeType type) {
        return _addAttribute(repoItem, attributeName, type);
    }

    @Override
    public RepoItemAttribute addAttribute(RepoItem repoItem, String attributeName, EAttributeType type, Object value) {
        RepoItemAttribute repoItemAttribute = _addAttribute(repoItem, attributeName, type);

        repoItemAttribute.setValue(value);

        return repoItemAttribute;
    }

    @Override
    public MetaAttribute createMetaAttribute(String name, EAttributeType type) {
        return createMetaAttribute(null, name, type, null);
    }

    @Override
    public MetaAttribute createMetaAttribute(String name, EAttributeType type, Object value) {
        return createMetaAttribute(null, name, type, value);
    }

    @Override
    public MetaAttribute createMetaAttribute(RepoItemAttribute ownerAttribute, String name, EAttributeType type) {
        return createMetaAttribute(ownerAttribute, name, type, null);
    }

    @Override
    public MetaAttribute createMetaAttribute(RepoItemAttribute ownerAttribute, String name, EAttributeType type, Object value) {
        MetaAttribute metaAttribute = new MetaAttribute();

        metaAttribute.setNameAndType(name, type);
        metaAttribute.setValue(value);
        metaAttribute.setOwnerAttribute(ownerAttribute);

        return metaAttribute;
    }

    private RepoItemAttribute _addAttribute(RepoItem repoItem, String attributeName, EAttributeType type) {
        RepoItemAttribute repoItemAttribute = repoItem.getAttribute(attributeName);

        if (repoItemAttribute == null) {
            repoItemAttribute = new RepoItemAttribute();
            repoItemAttribute.setNameAndType(attributeName, type);
            repoItemAttribute.setRepoItem(repoItem);

            repoItem.getRepoItemAttributes().add(repoItemAttribute);

            _persist(repoItemAttribute);
        }

        return repoItemAttribute;
    }

    private void _persist(Object repoItemAttribute) {
        entityManager.persist(repoItemAttribute);
    }
}
