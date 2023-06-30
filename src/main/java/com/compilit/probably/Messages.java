package com.compilit.probably;

import java.util.function.Predicate;

final class Messages {

  static final String SUCCESSFUL = "Nothing to report";
  static final String UNSUCCESSFUL = "Processing of probable was unsuccessful";
  static final String NO_MESSAGE_AVAILABLE = "No message available";
  static final String MESSAGE_FORMAT_ERROR = "Unable to format probable message, reason: ";

  private Messages() {}

  public static <T> String failedPredicate(Predicate<T> predicate) {
    return String.format("Predicate failed for this probable: %s", predicate);
  }
}
