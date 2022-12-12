package com.github.taigacat.awesomeblog.test;

import com.amazonaws.services.dynamodbv2.local.main.ServerRunner;
import com.amazonaws.services.dynamodbv2.local.server.DynamoDBProxyServer;
import com.github.taigacat.awesomeblog.infrastructure.db.dynamodb.common.DynamoDbTableType;
import com.github.taigacat.awesomeblog.infrastructure.db.dynamodb.repository.DynamoDbRepository;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.annotation.Value;
import io.micronaut.context.env.Environment;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Requires(property = "dynamodb.local.port")
@Requires(env = {Environment.TEST})
@Singleton
public class DynamoDbLocal {

  private static final Logger LOGGER = LoggerFactory.getLogger(DynamoDbLocal.class);

  private DynamoDBProxyServer server;
  private final DynamoDbRepository dynamoRepository;

  private final String dynamoDbLocalPort;

  public DynamoDbLocal(
      @Value("${dynamodb.local.port}") String port,
      DynamoDbRepository dynamoRepository
  ) {
    this.dynamoRepository = dynamoRepository;
    this.dynamoDbLocalPort = port;
    System.setProperty("sqlite4java.library.path", "target/native-libs");
  }

  public void start() {
    try {
      LOGGER.info("DynamoDB Local - start");
      this.server = ServerRunner.createServerFromCommandLineArgs(new String[]{
          "-inMemory",
          "-port",
          dynamoDbLocalPort
      });
      this.server.start();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    LOGGER.info("create DynamoDB tables");
    dynamoRepository.createTable(DynamoDbTableType.OBJECT_TABLE);
    dynamoRepository.createTable(DynamoDbTableType.RELATION_TABLE);
  }

  public void stop() {
    try {
      LOGGER.info("DynamoDB Local - stop");
      this.server.stop();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}
