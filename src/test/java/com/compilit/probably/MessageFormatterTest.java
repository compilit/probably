package com.compilit.probably;

import static com.compilit.probably.testutil.TestValue.TEST_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.IllegalFormatException;
import org.junit.jupiter.api.Test;

class MessageFormatterTest {

  @Test
  void formatMessage_noArgs_shouldReturnInput() {
    assertThat(MessageFormatter.formatMessage(TEST_MESSAGE)).isEqualTo(TEST_MESSAGE);
  }

  @Test
  void formatMessage_args_shouldReturnFormattedInput() {
    assertThat(MessageFormatter.formatMessage("test %s", "test")).isEqualTo("test test");
  }

  @Test
  void formatMessage_nullArgs_shouldReturnInput() {
    assertThat(MessageFormatter.formatMessage(TEST_MESSAGE, null)).isEqualTo(TEST_MESSAGE);
  }

  @Test
  void formatMessage_emptyArgs_shouldReturnInput() {
    assertThat(MessageFormatter.formatMessage(TEST_MESSAGE, new Object[0])).isEqualTo(TEST_MESSAGE);
  }

  @Test
  void formatMessage_argsWithInvalidMessage_shouldReturnFormattedInput() {
    assertThat(MessageFormatter.formatMessage("test %t", "test")).isEqualTo(String.format(Messages.MESSAGE_FORMAT_ERROR, "Conversion = 't'"));
  }
}