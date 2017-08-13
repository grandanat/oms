package org.mst.ubs.oms.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by MarianStrugaru on 8/13/2017.
 */
public class CollectionUtils {

    public static <E> List<E> asList(Collection<E> collection) {
        if (collection instanceof List) {
            return (List) collection;
        } else {
            return new ArrayList<>(collection);
        }
    }
}
