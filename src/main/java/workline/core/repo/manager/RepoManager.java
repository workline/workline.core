package workline.core.repo.manager;

import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import loggee.api.Logged;
import vrds.model.RepoItem;
import vrds.model.RepoItemAttribute;
import vrds.model.attributetype.StringAttributeValueHandler;
import vrds.model.meta.TODO;
import vrds.model.meta.TODOTag;
import workline.core.api.internal.IConsistencyChecker;
import workline.core.api.internal.IRepoHandler;
import workline.core.consitencycheck.Dummy;
import workline.core.domain.EInheritenceType;
import workline.core.domain.ERepoItemState;
import workline.core.engine.constants.WorklineRepoConstants;
import workline.core.util.Primary;

@Logged
@Stateless
public class RepoManager implements IRepoManager {
    @Inject
    @Primary
    private EntityManager entityManager;
    @Inject
    private IRepoHandler repoHandler;

    @Inject
    @Dummy
    private IConsistencyChecker consistencyChecker;

    @Override
    @TODO(tags = { TODOTag.MISSING_IMPLEMENTATION, TODOTag.INHERITENCE })
    public void setValue(RepoItem repoItem, String attributeName, Object value) {
        RepoItemAttribute attribute = repoItem.getAttribute(attributeName);
        getInheritenceType(attribute).setValue(this, repoHandler, repoItem, attributeName, value);

        assertConsistencyIfActive(repoItem);
    }

    @Override
    public EInheritenceType getInheritenceType(RepoItemAttribute attribute) {
        EInheritenceType inheritenceType = EInheritenceType.valueOf(attribute.getMetaAttributeValue(WorklineRepoConstants.INHERITENCE_TYPE_META_ATTRIBUTE_NAME,
                StringAttributeValueHandler.getInstance()));

        return inheritenceType;
    }

    @Override
    public ERepoItemState getState(RepoItem repoItem) {
        ERepoItemState state = ERepoItemState.valueOf(repoItem.getValue(WorklineRepoConstants.STATE_ATTRIBUTE_NAME, StringAttributeValueHandler.getInstance()));
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
        Set<String> consistencyCheckerIds = repoItem.getValues(WorklineRepoConstants.CONSISTENCY_CHECKER_ATTRIBUTE_NAME, StringAttributeValueHandler.getInstance());
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
