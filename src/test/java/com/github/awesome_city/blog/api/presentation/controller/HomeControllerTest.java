package com.github.awesome_city.blog.api.presentation.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.serverless.proxy.internal.testutils.MockLambdaContext;
import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.micronaut.function.aws.proxy.MicronautLambdaHandler;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

@MicronautTest
class HomeControllerTest {

  private static MicronautLambdaHandler handler;
  private static final Context lambdaContext = new MockLambdaContext();

  @BeforeAll
  public static void setupSpec() {
    try {
      handler = new MicronautLambdaHandler();
    } catch (ContainerInitializationException e) {
      e.printStackTrace();
    }
  }

  @AfterAll
  public static void cleanupSpec() {
    handler.getApplicationContext().close();
  }

  @Test
  void testHandler() throws JsonProcessingException {
    AwsProxyRequest request = new AwsProxyRequest();
    request.setHttpMethod("GET");
    request.setPath("/");
    AwsProxyResponse response = handler.handleRequest(request, lambdaContext);
    assertEquals(200, response.getStatusCode());
    assertEquals("{\"message\":\"Hello World\"}", response.getBody());
  }
}
