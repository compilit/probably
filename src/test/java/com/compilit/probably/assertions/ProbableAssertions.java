package com.compilit.probably.assertions;

import com.compilit.probably.Probable;
import org.assertj.core.api.AbstractAssert;

public class ProbableAssertions<T> extends AbstractAssert<ProbableAssertions<T>, Probable<T>> {


  protected ProbableAssertions(Probable<T> probable) {
    super(probable, ProbableAssertions.class);
  }

  public ProbableAssertions<T> isSuccessfulProbable() {
    if (actual.isUnsuccessful()) {
      failWithMessage("Expected Probable to be successful, but was: %s %s", actual.geType(), actual.getMessage());
    }
    return this;
  }

  public ProbableAssertions<T> isUnsuccessfulProbable() {
    if (actual.isSuccessful()) {
      failWithMessage("Expected Probable to be unsuccessful");
    }
    return this;
  }

  public ProbableAssertions<T> hasContent() {
    if (!actual.hasContents()) {
      failWithMessage("Expected Probable to have content but was empty");
    }
    return this;
  }

  public ProbableAssertions<T> isEmpty() {
    if (!actual.isEmpty()) {
      failWithMessage("Expected Probable to have no content but it did");
    }
    return this;
  }

  public ProbableAssertions<T> containsContent(T content) {
    if (!actual.hasContents()) {
      failWithMessage("Expected Probable to have content but was empty");
    }
    if (!actual.get().equals(content)) {
      failWithMessage(
        "Expected Probable to have content equal to %s but was %s",
        content,
        actual.get()
      );
    }
    return this;
  }

  public ProbableAssertions<T> containsMessage(String message) {
    if (!actual.getMessage().equals(message)) {
      failWithMessage(
        "Expected Probable to have a message equal to %s but was %s",
        message,
        actual.getMessage()
      );
    }
    return this;
  }

  public static <T> ProbableAssertions<T> assertThat(Probable<T> actual) {
    return new ProbableAssertions<>(actual);
  }

}
