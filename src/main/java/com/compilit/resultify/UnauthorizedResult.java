package com.compilit.resultify;

class UnauthorizedResult<T> extends AbstractResult<T> {

  UnauthorizedResult() {
    super(ResultType.UNAUTHORIZED);
  }

  UnauthorizedResult(String message) {
    super(ResultType.UNAUTHORIZED, message);
  }
}
