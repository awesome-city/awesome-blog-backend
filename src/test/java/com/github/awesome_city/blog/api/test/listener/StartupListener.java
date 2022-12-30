package com.github.awesome_city.blog.api.test.listener;

import com.github.awesome_city.blog.api.test.DynamoDbLocal;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.context.event.StartupEvent;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Requires(env = {Environment.TEST})
@Singleton
public class StartupListener implements ApplicationEventListener<StartupEvent> {

  private static final Logger LOGGER = LoggerFactory.getLogger(StartupListener.class);

  private final DynamoDbLocal dynamoDbLocal;

  public StartupListener(DynamoDbLocal dynamoDbLocal) {
    this.dynamoDbLocal = dynamoDbLocal;
  }

  @Override
  public void onApplicationEvent(StartupEvent event) {
    LOGGER.info("in");
    dynamoDbLocal.start();
    LOGGER.info("end");
  }
}
