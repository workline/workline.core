package workline.core.consitencycheck;

import loggee.api.Logged;
import vrds.model.RepoItem;
import workline.core.api.internal.IConsistencyChecker;

@Dummy
@Logged
public class DummyConsistencyChecker implements IConsistencyChecker {

    @Override
    public boolean checkConsistency(String consistencyCheckerId, RepoItem repoItem) {
        return true;
    }

}
