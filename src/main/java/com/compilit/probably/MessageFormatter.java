package com.compilit.probably;

import java.util.IllegalFormatException;

class MessageFormatter {

  private MessageFormatter() {
  }

  static String formatMessage(String message, Object... formatArguments) {
    if (message == null) {
      return Messages.NO_MESSAGE_AVAILABLE;
    }
    if (formatArguments == null || formatArguments.length == 0) {
      return message;
    }
    try {
      return String.format(message, formatArguments);
    } catch (IllegalFormatException exception) {
      return Messages.MESSAGE_FORMAT_ERROR + exception.getMessage();
    }
  }

}
