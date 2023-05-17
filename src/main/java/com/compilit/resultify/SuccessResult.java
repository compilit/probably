package com.compilit.resultify;

class SuccessResult<T> extends AbstractResult<T> {

  SuccessResult() {
    super(ResultType.SUCCESS);
  }

  SuccessResult(T contents) {
    super(ResultType.SUCCESS, contents);
  }

}
