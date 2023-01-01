package com.github.awesome_city.blog.api.domain.common;

import com.github.awesome_city.blog.api.util.aspect.Log;
import javax.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Validatable {

  private static final Logger LOGGER = LoggerFactory.getLogger(Validatable.class);

  @Log
  public boolean validate(Validator validator) {
    var violations = validator.validate(this);
    if (violations.size() > 0) {
      LOGGER.warn("violations = " + violations);
      return false;
    } else {
      LOGGER.info("no violations");
      return true;
    }
  }
}
