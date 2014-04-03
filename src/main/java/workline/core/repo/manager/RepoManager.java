package workline.core.repo.manager;

import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;

import loggee.api.Logged;
import vrds.model.EAttributeType;
import vrds.model.MetaAttribute;
import vrds.model.RepoItem;
import vrds.model.RepoItemAttribute;
import vrds.model.attributetype.StringAttributeValueHandler;
import workline.core.api.internal.IConsistencyChecker;
import workline.core.api.internal.IRepoHandler;
import workline.core.api.internal.IRepoManager;
import workline.core.consitencycheck.Dummy;
import workline.core.domain.EInheritenceType;
import workline.core.domain.ERepoItemState;
import workline.core.engine.constants.WorklineRepoConstants;

@Logged
@Stateless
public class RepoManager implements IRepoManager {
    @Inject
    private IRepoHandler repoHandler;

    @Inject
    @Dummy
    private IConsistencyChecker consistencyChecker;

    @Override
    public void setValue(RepoItem repoItem, String attributeName, Object value) {
        RepoItemAttribute attribute = repoItem.getAttribute(attributeName);

        EInheritenceType inheritenceType = getInheritenceType(attribute);

        inheritenceType.setValue(this, repoHandler, repoItem, attributeName, value);

        assertConsistencyIfActive(repoItem);
    }

    @Override
    public RepoItemAttribute addAttribute(RepoItem repoItem, String attributeName, EAttributeType type, EInheritenceType inheritenceType,
            RepoItem inheritenceSource,
            Object value) {

        RepoItemAttribute attribute = repoHandler.addAttribute(repoItem, attributeName, type, value);

        addInheritenceType(attribute, inheritenceType);
        addInheritenceSource(attribute, inheritenceSource);

        return attribute;
    }

    @Override
    public MetaAttribute addInheritenceType(RepoItemAttribute ownerAttribute, EInheritenceType inheritenceType) {
        return repoHandler.createMetaAttribute(ownerAttribute, WorklineRepoConstants.INHERITENCE_TYPE_META_ATTRIBUTE_NAME, EAttributeType.STRING,
                inheritenceType.toString());
    }

    @Override
    public MetaAttribute addInheritenceSource(RepoItemAttribute ownerAttribute, RepoItem inheritenceSource) {
        return repoHandler.createMetaAttribute(ownerAttribute, WorklineRepoConstants.INHERITENCE_SOURCE_META_ATTRIBUTE_NAME, EAttributeType.REPO_ITEM,
                inheritenceSource);
    }

    @Override
    public EInheritenceType getInheritenceType(RepoItemAttribute attribute) {
        EInheritenceType inheritenceType;

        String inheritenceTypeAsString = attribute.getMetaAttributeValue(WorklineRepoConstants.INHERITENCE_TYPE_META_ATTRIBUTE_NAME,
                StringAttributeValueHandler.getInstance());

        if (inheritenceTypeAsString == null) {
            inheritenceType = EInheritenceType.OVERRIDE;
        } else {
            inheritenceType = EInheritenceType.valueOf(inheritenceTypeAsString);
        }

        return inheritenceType;
    }

    @Override
    public ERepoItemState getState(RepoItem repoItem) {
        ERepoItemState state;

        String statusAsString = repoHandler.getNonInheritingValue(repoItem, WorklineRepoConstants.STATE_ATTRIBUTE_NAME,
                StringAttributeValueHandler.getInstance());

        if (statusAsString == null) {
            state = null;
        } else {
            state = ERepoItemState.valueOf(statusAsString);
        }

        return state;
    }

    @Override
    public void setState(RepoItem repoItem, ERepoItemState state) {
        setValue(repoItem, WorklineRepoConstants.STATE_ATTRIBUTE_NAME, state.toString());
        assertConsistencyIfActive(repoItem);
    }

    @Override
    public boolean isActive(RepoItem repoItem) {
        boolean active = getState(repoItem) == ERepoItemState.ACTIVE;

        return active;
    }

    @Override
    public void checkConsistency(RepoItem repoItem) {
        Set<String> consistencyCheckerIds = repoItem.getValues(WorklineRepoConstants.CONSISTENCY_CHECKER_ATTRIBUTE_NAME,
                StringAttributeValueHandler.getInstance());
        for (String consistencyCheckerId : consistencyCheckerIds) {
            boolean consistent = consistencyChecker.checkConsistency(consistencyCheckerId, repoItem);
            if (!consistent) {
                throw new IllegalStateException("{" + repoItem.getId() + "} of the {" + repoItem.getRepoName()
                        + "} repository is inconsistent according to consistency checker with id {" + consistencyCheckerId + "}!");
            }
        }
    }

    private void assertConsistencyIfActive(RepoItem repoItem) {
        if (isActive(repoItem)) {
            checkConsistency(repoItem);
        }
    }
}
