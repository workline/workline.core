package tmp;

import vrds.model.meta.TODOTag;

@vrds.model.meta.TODO(tags = { TODOTag.MISSING_IMPLEMENTATION })
public interface TODO {
    @vrds.model.meta.TODO(tags = { TODOTag.SPECIFICATION_REQUIRED })
    public static final String NAME_OF_HIERARCHIAL_REPO = "Maybe we should find a common name for the hierarchial repos.";
    @vrds.model.meta.TODO(tags = { TODOTag.INHERITENCE })
    public static final String INHERITENCE_TYPE_OF_VARIABLE_CHANGES = "What if the inheritence type of a variable changes?";
}
