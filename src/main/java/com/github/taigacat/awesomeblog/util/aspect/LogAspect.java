package com.github.taigacat.awesomeblog.util.aspect;

import io.micronaut.aop.InterceptorBean;
import io.micronaut.aop.MethodInterceptor;
import io.micronaut.aop.MethodInvocationContext;
import jakarta.inject.Singleton;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
@InterceptorBean(Log.class)
public class LogAspect implements MethodInterceptor<Object, Object> {

  private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

  @Override
  public Object intercept(MethodInvocationContext<Object, Object> context) {
    long start = System.currentTimeMillis();
    String method = context.getMethodName()
        + "("
        + Arrays.stream(context.getArgumentTypes())
        .map(Class::getSimpleName)
        .collect(Collectors.joining(","))
        + ")";
    LOGGER.info(
        String.format("start [method = %s, class = %s]", method,
            context.getClass().getSimpleName()));
    Object result = context.proceed();
    long duration = System.currentTimeMillis() - start;
    LOGGER.info(
        String.format("end [method = %s, class = %s] duration: %d ms.", method,
            context.getClass().getSimpleName(), duration));
    return result;
  }
}
