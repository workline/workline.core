package workline.core.util;

import java.util.Collection;

public class CommonUtil {
    public boolean isNullOrEmpty(Collection<?> collection) {
        boolean nullOrEmpty = collection == null || collection.isEmpty();

        return nullOrEmpty;
    }
}
