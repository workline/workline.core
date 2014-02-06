package workline.core.repo.handler;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.SetJoin;

import loggee.api.Logged;
import vrds.model.EAttributeType;
import vrds.model.IValueWrapper;
import vrds.model.MetaAttribute;
import vrds.model.MetaAttribute_;
import vrds.model.RepoItem;
import vrds.model.RepoItemAttribute;
import vrds.model.RepoItemAttribute_;
import vrds.model.RepoItemValue;
import vrds.model.RepoItemValue_;
import vrds.model.attributetype.AttributeValueHandler;
import workline.core.api.internal.IRepoHandler;
import workline.core.engine.constants.WorklineRepoConstants;
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
    public <T, W extends IValueWrapper<T>> T getNonInheritingValue(RepoItem repoItem, String attributeName, AttributeValueHandler<T, W> attributeValueHandler) {
        return repoItem.getValue(attributeName, attributeValueHandler);
    }

    @Override
    public <T, W extends IValueWrapper<T>> Set<T> getInheritingValues(RepoItem repoItem, String attributeName, AttributeValueHandler<T, W> attributeValueHandler) {
        return repoItem.getValues(attributeName, attributeValueHandler);
    }

    @Override
    public MetaAttribute createMetaAttribute(RepoItemAttribute ownerAttribute, String name, EAttributeType type, Object value) {
        MetaAttribute metaAttribute = new MetaAttribute();

        metaAttribute.setNameAndType(name, type);
        metaAttribute.setValue(value);
        metaAttribute.setOwnerAttribute(ownerAttribute);

        if (ownerAttribute != null) {
            ownerAttribute.getMetaAttributes().add(metaAttribute);
        }

        entityManager.persist(metaAttribute);

        return metaAttribute;
    }

    @Override
    public void setValue(RepoItem repoItem, String attributeName, Object value) {
        setValue(repoItem, null, attributeName, value);
    }

    @Override
    public <T> void setSimpleValues(RepoItem repoItem, String attributeName, AttributeValueHandler<T, ?> attributeValueHandler, T ... values) {
        RepoItemAttribute attribute = repoItem.getAttribute(attributeName);

        if (values != null) {
            attributeValueHandler.setSimpleValues(attribute, Arrays.asList(values));
        } else {
            List<T> emptyList = Collections.emptyList();
            attributeValueHandler.setSimpleValues(attribute, emptyList);
        }
    }

    @Override
    public void setValue(RepoItem repoItem, RepoItem benefactor, String attributeName, Object value) {
        IValueWrapper<Object> valueWrapper = repoItem.getValueWrapper(attributeName, benefactor);

        if (valueWrapper == null) {
            IValueWrapper<?> newValueWrapper = repoItem.addValue(attributeName, value);
            newValueWrapper.setBenefactor(benefactor);
            entityManager.persist(newValueWrapper);
        } else {
            valueWrapper.setValue(value);
        }
    }

    @Override
    public Collection<RepoItemAttribute> getInheritors(RepoItem benefactor, String attributeName) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<RepoItemAttribute> query = criteriaBuilder.createQuery(RepoItemAttribute.class);
        query.distinct(true);

        Root<RepoItemAttribute> root = query.from(RepoItemAttribute.class);
        SetJoin<RepoItemAttribute, MetaAttribute> joinInheritorMetaAttributes = root.join(RepoItemAttribute_.metaAttributes, JoinType.LEFT);
        SetJoin<MetaAttribute, RepoItemValue> joinInheritorMetaAttributeRepoItemValues = joinInheritorMetaAttributes.join(MetaAttribute_.repoItemValues,
                JoinType.LEFT);

        Predicate predicate = criteriaBuilder.and(
                criteriaBuilder.equal(root.get(RepoItemAttribute_.name), attributeName),
                criteriaBuilder.equal(joinInheritorMetaAttributes.get(MetaAttribute_.name), WorklineRepoConstants.INHERITENCE_SOURCE_META_ATTRIBUTE_NAME),
                criteriaBuilder.equal(joinInheritorMetaAttributeRepoItemValues.get(RepoItemValue_.value), benefactor));
        query.select(root).where(predicate);

        List<RepoItemAttribute> inheritorList = entityManager.createQuery(query).getResultList();

        return inheritorList;
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
