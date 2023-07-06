package com.compilit.probably.assertions;

import com.compilit.probably.Probable;
import org.assertj.core.api.AbstractAssert;

public class ProbableAssertions<T> extends AbstractAssert<ProbableAssertions<T>, Probable<T>> {


  protected ProbableAssertions(Probable<T> probable) {
    super(probable, ProbableAssertions.class);
  }

  public ProbableAssertions<T> hasFailed() {
    if (!actual.hasFailed()) {
      failWithMessage("Expected Probable to have failed");
    }
    return this;
  }

  public ProbableAssertions<T> hasValue() {
    if (!actual.hasValue()) {
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

  public ProbableAssertions<T> hasValue(T value) {
    if (!actual.hasValue()) {
      failWithMessage("Expected Probable to have a value but was empty");
    }
    if (!actual.get().equals(value)) {
      failWithMessage(
        "Expected Probable to have a value equal to %s but was %s",
        value,
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
