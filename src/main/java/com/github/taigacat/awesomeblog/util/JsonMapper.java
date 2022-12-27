package com.github.taigacat.awesomeblog.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.inject.Singleton;

@Singleton
public class JsonMapper {

  private final ObjectMapper objectMapper;

  public JsonMapper() {
    objectMapper = new ObjectMapper();
    objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
  }

  public ObjectMapper getMapper() {
    return objectMapper;
  }
}
