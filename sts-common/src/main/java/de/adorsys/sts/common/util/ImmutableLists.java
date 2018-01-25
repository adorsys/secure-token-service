package de.adorsys.sts.common.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ImmutableLists {

    private static final List EMPTY_LIST = Collections.unmodifiableList(new ArrayList<>());

    public static <T> List<T> emptyList() {
        return EMPTY_LIST;
    }

    public static <T> List<T> of(T object, T... objects) {
        ArrayList<T> list = new ArrayList<>();

        list.add(object);

        if(objects != null) {
            list.addAll(Arrays.asList(objects));
        }

        return Collections.unmodifiableList(list);
    }

    private ImmutableLists() {
        throw new IllegalStateException("not supported");
    }
}
