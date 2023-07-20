package com.compilit.probably;

final class Messages {
  static final String BASE_LOG_MESSAGE = "Probable.%s(%s, %s)";
  static final String BASE_LOG_MESSAGE_WITH_CUSTOM_MESSAGE = "Probable.%s(%s, %s), log message: %s";
  static final String TRANSFORMED_INTO_STREAM = "transformed value into stream";
  static final String TRANSFORMED_INTO_EMPTY_STREAM = "transformed into empty stream";
  static final String ACCEPT_SUCCESSFUL = "accept() successful";
  static final String ACCEPT_FAILED = "accept() failed";
  static final String RUN_CALLED = "run() successful";
  static final String RUN_NOT_CALLED = "run() failed";
  static final String TEST_CALL_FAILED = "test() call failed";
  static final String MAP_APPLIED = "map() applied";
  static final String MAP_NOT_APPLIED = "map() not applied";
  static final String FLATMAP_APPLIED = "flatMap() applied";
  static final String FLATMAP_NOT_APPLIED = "flatMap() not applied";
  static final String NOTHING_TO_REPORT = "Nothing to report";
  static final String MESSAGE_FORMAT_ERROR = "Unable to format probable message, reason: %s";

  private Messages() {}

  static String testCallSuccessful(boolean outcome) {
    return String.format("test() called successfully, outcome: %b", outcome);
  }
  static String messageFormatException(Exception exception) {
    return String.format(MESSAGE_FORMAT_ERROR, exception.getMessage());
  }
  static <T> String failedPredicate(Probable<T> probable) {
    return String.format("Predicate failed for this probable: %s", probable);
  }
  static String paramRequired(String param) {
    return String.format("%s cannot be null.", param);
  }
  static String messageRequired() {
    return paramRequired("message");
  }

  static String exceptionWasThrown(Exception e) {
    if (e.getMessage() != null) {
      return e.getMessage();
    }
    return String.format("%s was thrown without any message", e.getClass().getTypeName());
  }
}
