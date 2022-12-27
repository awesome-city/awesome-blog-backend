package com.github.taigacat.awesomeblog.util;

import java.util.HashSet;
import java.util.Set;

public class CollectionUtils {

  public static <T> Set<T> differenceSet(Set<T> a, Set<T> b) {
    if (a == null) {
      a = new HashSet<>();
    }

    if (b == null) {
      b = new HashSet<>();
    }

    final Set<T> setA = new HashSet<>(a);
    setA.removeAll(b);
    return setA;
  }
}
