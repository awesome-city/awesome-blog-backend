package com.github.taigacat.awesomeblog.constant.error;

import io.micronaut.http.HttpStatus;
import java.io.Serializable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public abstract class MyException extends Error implements Serializable {

  private String message;
  private String code;

  private HttpStatus status;

  public MyException(HttpStatus status, String code, String message) {
    super(message);
    this.status = status;
    this.code = code;
    this.message = message;
  }
}
