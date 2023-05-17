package com.compilit.resultify;

class UnprocessableResult<T> extends AbstractResult<T> {

  UnprocessableResult() {
    super(ResultType.UNPROCESSABLE);
  }

  UnprocessableResult(String message) {
    super(ResultType.UNPROCESSABLE, message);
  }

}
