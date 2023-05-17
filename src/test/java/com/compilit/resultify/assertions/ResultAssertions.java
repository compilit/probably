package com.compilit.resultify.assertions;

import com.compilit.resultify.Result;
import com.compilit.resultify.ResultType;
import org.assertj.core.api.AbstractAssert;

public class ResultAssertions<T> extends AbstractAssert<ResultAssertions<T>, Result<T>> {


  protected ResultAssertions(Result<T> result) {
    super(result, ResultAssertions.class);
  }

  public ResultAssertions<T> isANotFoundResult() {
    if (!actual.getResultType().equals(ResultType.NOT_FOUND)) {
      failWithMessage("Expected Result to be NOT_FOUND, but was %s", actual.getResultType());
    }
    return this;
  }

  public ResultAssertions<T> isAnUnprocessableResult() {
    if (!actual.getResultType().equals(ResultType.UNPROCESSABLE)) {
      failWithMessage("Expected Result to be UNPROCESSABLE, but was %s", actual.getResultType());
    }
    return this;
  }

  public ResultAssertions<T> isAnErrorOccurredResult() {
    if (!actual.getResultType().equals(ResultType.ERROR_OCCURRED)) {
      failWithMessage("Expected Result to be ERROR_OCCURRED, but was %s", actual.getResultType());
    }
    return this;
  }

  public ResultAssertions<T> isAnUnauthorizedResult() {
    if (!actual.getResultType().equals(ResultType.UNAUTHORIZED)) {
      failWithMessage("Expected Result to be UNAUTHORIZED, but was %s", actual.getResultType());;
    }
    return this;
  }

  public ResultAssertions<T> isValidSuccessResult() {
    if (actual.isUnsuccessful()) {
      failWithMessage("Expected Result to be successful, but was: %s %s", actual.getResultType(), actual.getMessage());
    }
    return this;
  }

  public ResultAssertions<T> hasContent() {
    if (!actual.hasContents()) {
      failWithMessage("Expected Result to have content but was empty");
    }
    return this;
  }

  public ResultAssertions<T> hasContentEqualTo(T contents) {
    if (!actual.hasContents()) {
      failWithMessage("Expected Result to have content but was empty");
    }
    if (!actual.get().equals(contents)) {
      failWithMessage("Expected Result to have content equal to %s", contents);
    }
    return this;
  }

  public ResultAssertions<T> isEmpty() {
    if (!actual.isEmpty()) {
      failWithMessage("Expected Result to have no content but it did");
    }
    return this;
  }

  public ResultAssertions<T> containsContent(T content) {
    if (!actual.hasContents()) {
      failWithMessage("Expected Result to have content but was empty");
    }
    if (!actual.get().equals(content)) {
      failWithMessage(
        "Expected Result to have content equal to %s but was %s",
        content,
        actual.get()
      );
    }
    return this;
  }

  public ResultAssertions<T> isValidUnsuccessfulResult() {
    if (actual.isSuccessful()) {
      failWithMessage("Expected Result to be unsuccessful");
    }
    return this;
  }

  public ResultAssertions<T> containsMessage(String message) {
    if (!actual.getMessage().equals(message)) {
      failWithMessage(
        "Expected Result to have a message equal to %s but was %s",
        message,
        actual.getMessage()
      );
    }
    return this;
  }

  public static <T> ResultAssertions<T> assertThat(Result<T> actual) {
    return new ResultAssertions<>(actual);
  }

}
