package com.compilit.probably;

import java.util.IllegalFormatException;

final class MessageFormatter {

  private MessageFormatter() {
  }

  static String formatMessage(String message, Object... formatArguments) {
    if (message == null) {
      return Messages.NOTHING_TO_REPORT;
    }
    if (formatArguments == null || formatArguments.length == 0) {
      return message;
    }
    try {
      return String.format(message, formatArguments);
    } catch (IllegalFormatException exception) {
      return Messages.messageFormatException(exception);
    }
  }

}
