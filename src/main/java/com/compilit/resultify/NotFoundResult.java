package com.compilit.resultify;

class NotFoundResult<T> extends AbstractResult<T> {

  NotFoundResult() {
    super(ResultType.NOT_FOUND);
  }

  NotFoundResult(String message) {
    super(ResultType.NOT_FOUND, message);
  }

}

