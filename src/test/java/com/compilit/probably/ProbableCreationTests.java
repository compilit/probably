package com.compilit.probably;

import static com.compilit.probably.testutil.TestValue.TEST_MESSAGE;
import static com.compilit.probably.testutil.TestValue.TEST_VALUE;

import com.compilit.probably.testutil.ProbableAssertions;
import com.compilit.probably.testutil.TestValue;
import java.util.function.Supplier;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class ProbableCreationTests {

  @Test
  void of_successfulSupplier_shouldReturnProbableValue() {
    Supplier<String> supplier = () -> TestValue.TEST_VALUE;
    var probable = Probable.of(supplier);
    ProbableAssertions.assertThat(probable)
                      .hasValue(TestValue.TEST_VALUE);
  }

  @Test
  void of_unsuccessfulSupplier_shouldReturnProbableFailure() {
    var exception = new RuntimeException(TestValue.TEST_VALUE);
    var supplier = new Supplier<Probable<String>>() {
      @Override
      public Probable<String> get() {
        throw exception;
      }
    };
    var message = exception.getMessage();
    var probable = Probable.of(supplier);
    ProbableAssertions.assertThat(probable).hasFailed().hasMessage(message);
  }

  @Test
  void of_nested_shouldReturnProbableValue() {
    var probable = Probable.of(() -> Probable.of(123)
                                             .flatMap(x -> Probable.of(String.valueOf(x))
                                                                   .flatMap(z -> Probable.of(Integer.valueOf(z)))))
                           .map(Probable::getValue);
    ProbableAssertions.assertThat(probable)
                      .hasValue(123);
  }

  @Test
  void of_value_shouldReturnProbableValue() {
    var probable = Probable.of(TestValue.TEST_VALUE);
    ProbableAssertions.assertThat(probable)
                      .hasValue(TestValue.TEST_VALUE);
  }

  @Test
  void of_null_shouldReturnProbableNothing() {
    var probable = Probable.of((Object) null);
    ProbableAssertions.assertThat(probable).isEmpty();
  }

  @Test
  void value_shouldReturnValueProbable() {
    ProbableAssertions.assertThat(Probable.value(TEST_VALUE))
                      .hasValue(TEST_VALUE);
  }

  @Test
  void value$message_shouldReturnValueProbableWithMessage() {
    ProbableAssertions.assertThat(Probable.value(TEST_VALUE, TEST_MESSAGE))
                      .hasMessage(TEST_MESSAGE)
                      .hasValue(TEST_VALUE);
  }

  @Test
  void nothing_shouldReturnEmptyProbable() {
    ProbableAssertions.assertThat(Probable.nothing())
                      .isEmpty();
  }

  @Test
  void nothing$message_shouldReturnEmptyProbableWithMessage() {
    ProbableAssertions.assertThat(Probable.nothing(TEST_MESSAGE))
                      .hasMessage(TEST_MESSAGE)
                      .isEmpty();
  }

  @Test
  void failure_shouldReturnFailedProbable() {
    ProbableAssertions.assertThat(Probable.failure(TestValue.TEST_MESSAGE))
                      .hasFailed()
                      .hasMessage(TestValue.TEST_MESSAGE);
  }

  @Test
  void failure_withFormattedMessage_shouldReturnFormattedMessage() {
    var expected = String.format("test %s", "test");
    var probable = Probable.failure("test %s", "test");
    Assertions.assertThat(probable.getMessage()).isEqualTo(expected);
    ProbableAssertions.assertThat(probable)
                      .hasFailed()
                      .hasMessage(expected);
  }

}
