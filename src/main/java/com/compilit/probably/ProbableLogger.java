package com.compilit.probably;

import static com.compilit.probably.Probable.LOGGER;

import org.slf4j.event.Level;

class ProbableLogger {

  private ProbableLogger() {}

  public static <T> void log(Probable<T> probable, Level level, String message, Object[] args) {
    String probableMessage = createLogMessage(probable, message);
    var encounteredException = probable.getException();
    if (encounteredException != null) {
      LOGGER.atLevel(level).log(probableMessage, encounteredException);
    } else {
      LOGGER.atLevel(level).log(probableMessage, args);
    }
  }

  static void logDebugEvent(Probable<?> probable, String message) {
    if (LOGGER.isDebugEnabled()) {
      var actualMessage = createLogMessage(probable, message);
      LOGGER.debug(actualMessage);
    }
  }

  static String createLogMessage(Probable<?> probable, String logMessage) {
    if (logMessage == null) {
      return String.format(
        Messages.BASE_LOG_MESSAGE,
        probable.getClass().getSimpleName(),
        probable.getValue(),
        probable.getMessage()
      );
    }
    return String.format(
      Messages.BASE_LOG_MESSAGE_WITH_CUSTOM_MESSAGE,
      probable.getClass().getSimpleName(),
      probable.getValue(),
      probable.getMessage(),
      logMessage
    );
  }

}
