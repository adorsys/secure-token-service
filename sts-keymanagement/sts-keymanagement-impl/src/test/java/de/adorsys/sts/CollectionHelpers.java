package de.adorsys.sts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CollectionHelpers {

    public static <T> List<T> asList(T first, T ... others) {
        List<T> list = new ArrayList<>();
        list.add(first);

        Collections.addAll(list, others);

        return list;
    }
}
