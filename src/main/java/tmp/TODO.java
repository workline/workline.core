package tmp;

import vrds.model.meta.TODOTag;

@vrds.model.meta.TODO(tags = { TODOTag.MISSING_IMPLEMENTATION })
public interface TODO {
    @vrds.model.meta.TODO(tags = { TODOTag.MISSING_IMPLEMENTATION })
    public static final String EXPRESSION_AS_STRING_VALUE = "Normal string attributes should be able to store expressions. Need to check and evaluate these. Mark string value with some special starting characters.";
    @vrds.model.meta.TODO(tags = { TODOTag.SPECIFICATION_REQUIRED })
    public static final String NAME_OF_HIERARCHIAL_REPO = "Maybe we should find a common name for the hierarchial repos.";
    @vrds.model.meta.TODO(tags = { TODOTag.INHERITENCE })
    public static final String INHERITENCE_TYPE_OF_VARIABLE_CHANGES = "What if the inheritence type or -source of a variable changes? Need to adjust value accordingly.";
    @vrds.model.meta.TODO(tags = { TODOTag.INHERITENCE })
    public static final String INHERITENCE_FOR_NEW_REPO_ITEMS = "Probably should do the same as when the inheritence tpye or -source changes.";
}
