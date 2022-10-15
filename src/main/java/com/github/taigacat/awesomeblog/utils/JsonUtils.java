package com.github.taigacat.awesomeblog.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class JsonUtils {

	private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);

	private static ObjectMapper mapper;

	private JsonUtils() {
	}

	public static ObjectMapper getMapper() {
		if (mapper == null) {
			mapper = new ObjectMapper();
		}
		return mapper;
	}

	public static <T> Optional<T> readValue(String jsonStr, TypeReference<T> typeReference) {
		if (jsonStr == null || jsonStr.equals("")) {
			return Optional.empty();
		}
		try {
			return Optional.ofNullable(getMapper().readValue(jsonStr, typeReference));
		} catch (JsonProcessingException e) {
			logger.error("failed to parse json. [jsonStr = " + jsonStr + "]");
			return Optional.empty();
		}
	}

	public static Optional<String> writeValueAsString(Object object) {
		try {
			return Optional.ofNullable(getMapper().writeValueAsString(object));
		} catch (JsonProcessingException e) {
			logger.error("failed to write value as string.");
			return Optional.empty();
		}
	}
}
