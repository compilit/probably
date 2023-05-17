package com.compilit.resultify;

class ErrorOccurredResult<T> extends AbstractResult<T> {

  ErrorOccurredResult(String message) {
    super(ResultType.ERROR_OCCURRED, message);
  }

  ErrorOccurredResult(Exception exception, String message) {
    super(ResultType.ERROR_OCCURRED, exception, message);
  }

}
