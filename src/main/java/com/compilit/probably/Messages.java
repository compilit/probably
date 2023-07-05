package com.compilit.probably;

import java.util.function.Predicate;

final class Messages {

  static final String VALUE = "Processing of probable lead to a result";
  static final String FAILURE = "Processing of probable lead to an exception";
  static final String EMPTY = "Processing of probable lead to an empty result";
  static final String NO_MESSAGE_AVAILABLE = "No message available";
  static final String MESSAGE_FORMAT_ERROR = "Unable to format probable message, reason: ";

  private Messages() {}

  public static <T> String failedPredicate(Predicate<T> predicate) {
    return String.format("Predicate failed for this probable: %s", predicate);
  }
}
