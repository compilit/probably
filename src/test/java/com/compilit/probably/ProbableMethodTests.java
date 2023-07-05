package com.compilit.probably;

import static com.compilit.probably.Messages.JUST;
import static com.compilit.probably.testutil.TestValue.TEST_CONTENT;
import static com.compilit.probably.testutil.TestValue.TEST_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;

import com.compilit.probably.Probable.Type;
import com.compilit.probably.testutil.TestValue;
import org.junit.jupiter.api.Test;

class ProbableMethodTests {

  @Test
  void isEmpty_emptyProbable_shouldReturnTrue() {
    var probable = Probable.empty();
    assertThat(probable.isEmpty()).isTrue();
  }

  @Test
  void isEmpty_filledProbable_shouldReturnFalse() {
    var probable = Probable.value("test");
    assertThat(probable.isEmpty()).isFalse();
  }

  @Test
  void getProbableType_shouldReturnProbableType() {
    assertThat(Probable.empty().geType())
      .isEqualTo(Type.VALUE);
    assertThat(Probable.failure(TEST_MESSAGE).geType())
      .isEqualTo(Type.EMPTY);
  }

  @Test
  void hasContents_withContents_shouldReturnTrue() {
    var probable = Probable.value(TestValue.TEST_CONTENT);
    assertThat(probable.hasContents()).isTrue();
  }

  @Test
  void hasContents_withoutContents_shouldReturnFalse() {
    var probable = Probable.empty();
    assertThat(probable.hasContents()).isFalse();
  }

  @Test
  void isEmpty_withContents_shouldReturnFalse() {
    var probable = Probable.value(TestValue.TEST_CONTENT);
    assertThat(probable.isEmpty()).isFalse();
  }

  @Test
  void isEmpty_withoutContents_shouldReturnTrue() {
    var probable = Probable.empty();
    assertThat(probable.isEmpty()).isTrue();
  }

  @Test
  void getOptionalContents_shouldReturnContentsAsOptional() {
    var probable = Probable.value(TestValue.TEST_CONTENT);
    assertThat(probable.get()).contains(TestValue.TEST_CONTENT);
  }

  @Test
  void getContents_shouldReturnContents() {
    var probable = Probable.value(TestValue.TEST_CONTENT);
    assertThat(probable.get()).isEqualTo(TestValue.TEST_CONTENT);
  }

  @Test
  void thenApply_intProbableToString_shouldReturnString() {
    var input = 123;
    var expected = "123";
    var probable = Probable.value(input);
    var actual = probable.map(String::valueOf);
    assertThat(actual.get()).isEqualTo(expected);
  }

  @Test
  void thenApply_successful_shouldApplyFunction() {
    var expected = "10";
    var probable = Probable.value(10);
    Probable<String> actual = probable.map(String::valueOf);
    assertThat(actual.get()).contains(expected);
  }

  @Test
  void thenApply_failed_shouldReturnProbableWithMessage() {
    var probable = Probable.failure(TEST_MESSAGE);
    var actual = probable.map(x -> TEST_CONTENT);
    assertThat(actual.getMessage()).isEqualTo(TEST_MESSAGE);
  }

  @Test
  void getNullableContents_null_shouldReturnNull() {
    var errorProbable = Probable.failure(TEST_MESSAGE);
    assertThat(errorProbable.get()).isNull();
  }

  @Test
  void getMessage_shouldReturnMessage() {
    var errorMessage = "I am error";
    var errorProbable = Probable.failure(errorMessage);
    assertThat(errorProbable.getMessage()).isEqualTo(errorMessage);
    var successfulProbable = Probable.empty();
    assertThat(successfulProbable.getMessage()).isEqualTo(JUST);
  }

  @Test
  void getMessage_withFormattedMessage_shouldReturnFormattedMessage() {
    var expected = String.format("I am error %s %s %s %s", "test1", "test2", "someValue", "test3");
    var actual = Probable.failure("I am error %s %s %s %s", "test1", "test2", "someValue", "test3");
    assertThat(actual.getMessage()).isEqualTo(expected);
  }

}
