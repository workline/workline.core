package workflow.core.meta;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface SPECIFICATION_REQUIRED {
    SPECIFICATION_REQUIEREDTag[] tags() default {};

    String value() default "";
}
