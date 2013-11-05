package workflow.core.meta;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface BEWARE {
    BEWARETag[] tags() default {};

    String value() default "";
}
