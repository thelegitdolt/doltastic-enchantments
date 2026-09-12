package com.dolthhaven.doltasticenchantments.core.utils;

import java.util.Optional;
import java.util.SequencedCollection;

public class CSE20Util {
    public static <T> Optional<T> tryUnwrapSingleton(SequencedCollection<T> collection) {
        return Optional.ofNullable(collection.size() == 1 ? collection.getFirst() : null);
    }
}
