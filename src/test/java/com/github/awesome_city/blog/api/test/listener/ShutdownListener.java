package com.github.awesome_city.blog.api.test.listener;

import com.github.awesome_city.blog.api.test.DynamoDbLocal;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.context.event.ShutdownEvent;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Requires(env = {Environment.TEST})
@Singleton
public class ShutdownListener implements ApplicationEventListener<ShutdownEvent> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ShutdownListener.class);

  private final DynamoDbLocal dynamoDbLocal;

  public ShutdownListener(DynamoDbLocal dynamoDbLocal) {
    this.dynamoDbLocal = dynamoDbLocal;
  }

  @Override
  public void onApplicationEvent(ShutdownEvent event) {
    LOGGER.info("in");
    dynamoDbLocal.stop();
    LOGGER.info("end");
  }
}
