package com.github.taigacat.awesomeblog.presentation.common;

import com.github.taigacat.awesomeblog.constant.error.MyException;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;

@Produces
@Singleton
@Requires(classes = {MyException.class, ExceptionHandler.class})
public class ExceptionHandlerImpl implements
    ExceptionHandler<Throwable, HttpResponse<ErrorResponse>> {

  @Override
  public HttpResponse<ErrorResponse> handle(HttpRequest request, Throwable e) {
    if (e instanceof MyException myException) {
      return HttpResponse.serverError(new ErrorResponse(myException))
          .status(myException.getStatus());
    }

    return HttpResponse.serverError(new ErrorResponse("E999", "unknown")).status(
        HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
