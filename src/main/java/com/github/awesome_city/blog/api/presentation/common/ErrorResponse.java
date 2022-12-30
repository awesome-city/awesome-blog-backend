package com.github.awesome_city.blog.api.presentation.common;

import com.github.awesome_city.blog.api.constant.error.MyException;
import lombok.Getter;

@Getter
public class ErrorResponse {

  private final String code;
  private final String message;

  public ErrorResponse(MyException e) {
    this.code = e.getCode();
    this.message = e.getMessage();
  }

  public ErrorResponse(String code, String message) {
    this.code = code;
    this.message = message;
  }

}
