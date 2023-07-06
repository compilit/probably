package com.compilit.probably;

import static com.compilit.probably.Messages.NOTHING;
import static com.compilit.probably.testutil.TestValue.TEST_VALUE;
import static com.compilit.probably.testutil.TestValue.TEST_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;

import com.compilit.probably.Probable.Type;
import com.compilit.probably.testutil.TestValue;
import org.junit.jupiter.api.Test;

class ProbableMethodTests {

  @Test
  void isEmpty_emptyProbable_shouldReturnTrue() {
    var probable = Probable.nothing();
    assertThat(probable.isEmpty()).isTrue();
  }

  @Test
  void isEmpty_filledProbable_shouldReturnFalse() {
    var probable = Probable.value("test");
    assertThat(probable.isEmpty()).isFalse();
  }

  @Test
  void getProbableType_shouldReturnProbableType() {
    assertThat(Probable.nothing().getType())
      .isEqualTo(Type.NOTHING);
    assertThat(Probable.failure(TEST_MESSAGE).getType())
      .isEqualTo(Type.FAILURE);
  }

  @Test
  void hasValue_withContents_shouldReturnTrue() {
    var probable = Probable.value(TestValue.TEST_VALUE);
    assertThat(probable.hasValue()).isTrue();
  }

  @Test
  void hasValue_withoutContents_shouldReturnFalse() {
    var probable = Probable.nothing();
    assertThat(probable.hasValue()).isFalse();
  }

  @Test
  void isEmpty_withContents_shouldReturnFalse() {
    var probable = Probable.value(TestValue.TEST_VALUE);
    assertThat(probable.isEmpty()).isFalse();
  }

  @Test
  void isEmpty_withoutContents_shouldReturnTrue() {
    var probable = Probable.nothing();
    assertThat(probable.isEmpty()).isTrue();
  }

  @Test
  void get_shouldReturnContentsAsOptional() {
    var probable = Probable.value(TestValue.TEST_VALUE);
    assertThat(probable.get()).contains(TestValue.TEST_VALUE);
  }

  @Test
  void get_shouldReturnContents() {
    var probable = Probable.value(TestValue.TEST_VALUE);
    assertThat(probable.get()).isEqualTo(TestValue.TEST_VALUE);
  }

  @Test
  void map_intProbableToString_shouldReturnString() {
    var input = 123;
    var expected = "123";
    var probable = Probable.value(input);
    var actual = probable.map(String::valueOf);
    assertThat(actual.get()).isEqualTo(expected);
  }

  @Test
  void map_successful_shouldApplyFunction() {
    var expected = "10";
    var probable = Probable.value(10);
    Probable<String> actual = probable.map(String::valueOf);
    assertThat(actual.get()).contains(expected);
  }

  @Test
  void map_failed_shouldReturnProbableWithMessage() {
    var probable = Probable.failure(TEST_MESSAGE);
    var actual = probable.map(x -> TEST_VALUE);
    assertThat(actual.getMessage()).isEqualTo(TEST_MESSAGE);
  }

  @Test
  void get_null_shouldReturnNull() {
    var errorProbable = Probable.failure(TEST_MESSAGE);
    assertThat(errorProbable.get()).isNull();
  }

  @Test
  void getMessage_shouldReturnMessage() {
    var errorMessage = "I am error";
    var errorProbable = Probable.failure(errorMessage);
    assertThat(errorProbable.getMessage()).isEqualTo(errorMessage);
    var successfulProbable = Probable.nothing();
    assertThat(successfulProbable.getMessage()).isEqualTo(NOTHING);
  }

  @Test
  void getMessage_withFormattedMessage_shouldReturnFormattedMessage() {
    var expected = String.format("I am error %s %s %s %s", "test1", "test2", "someValue", "test3");
    var actual = Probable.failure("I am error %s %s %s %s", "test1", "test2", "someValue", "test3");
    assertThat(actual.getMessage()).isEqualTo(expected);
  }

  @Test
  void or_withValue_shouldReturnTheOriginalProbable() {
    var probable = Probable.value(TEST_VALUE);
    assertThat(probable.or(Probable.nothing())).isEqualTo(probable);
  }

  @Test
  void or_empty_shouldReturnTheOtherProbable() {
    var probable = Probable.<String>nothing();
    var otherProbable = Probable.value(TEST_VALUE);
    assertThat(probable.or(otherProbable)).isEqualTo(otherProbable);
  }

  @Test
  void or_failed_shouldReturnTheOtherProbable() {
    var probable = Probable.<String>failure(TEST_MESSAGE);
    var otherProbable = Probable.value(TEST_VALUE);
    assertThat(probable.or(otherProbable)).isEqualTo(otherProbable);
  }

}
