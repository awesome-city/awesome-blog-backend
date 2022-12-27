package com.github.taigacat.awesomeblog.util.micronaut;

import io.micronaut.core.beans.BeanIntrospection;
import io.micronaut.core.beans.BeanWrapper;
import io.micronaut.core.convert.exceptions.ConversionErrorException;

public final class BeanUtil {

  public static <T, U> U createAndCopy(T src, Class<U> clazz) {
    U target = BeanIntrospection.getIntrospection(clazz).instantiate();
    copy(src, target);
    return target;
  }

  public static <T, U> void copy(T src, U target) {
    BeanWrapper<T> srcWrapper = BeanWrapper.getWrapper(src);
    BeanWrapper<U> dstWrapper = BeanWrapper.getWrapper(target);
    srcWrapper.getBeanProperties().forEach(b -> {
      srcWrapper.getProperty(b.getName(), b.getType()).ifPresent(value -> {
        try {
          dstWrapper.setProperty(b.getName(), value);
          // Ignore properties that cannot be copied.
        } catch (ConversionErrorException ignored) {
        }
      });
    });
  }
}
