package com.compilit.probably;

import static com.compilit.probably.testutil.TestValue.TEST_VALUE;
import static org.assertj.core.api.Assertions.assertThat;

import com.compilit.probably.Probable.Type;
import com.compilit.probably.assertions.ProbableAssertions;
import com.compilit.probably.testutil.TestValue;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class ProbableCreationTests {

  @Test
  void empty_shouldReturnEmptyProbable() {
    ProbableAssertions.assertThat(Probable.nothing())
                      .isEmpty();
  }

  @Test
  void value_shouldReturnValueProbable() {
    ProbableAssertions.assertThat(Probable.value(TEST_VALUE))
                      .hasValue(TEST_VALUE);
  }

  @Test
  void failure_shouldReturnFailedProbable() {
    ProbableAssertions.assertThat(Probable.failure(TestValue.TEST_MESSAGE))
                      .hasFailed()
                      .containsMessage(TestValue.TEST_MESSAGE);
  }

  @Test
  void failure_withFormattedMessage_shouldReturnFormattedMessage() {
    var expected = String.format("test %s", "test");
    var probable = Probable.failure("test %s", "test");
    Assertions.assertThat(probable.getMessage()).isEqualTo(expected);
    Assertions.assertThat(probable.getType()).isEqualTo(Type.FAILURE);
  }

//  @Test
//  void transform_shouldReturnEmptyProbableWithCorrectStatus() {
//    var probable = Probable.value("test");
//    assertThat(probable.isEmpty()).isFalse();
//    var actual = Probable.<Integer>transform(probable);
//    ProbableAssertions.assertThat(actual)
//                      .hasValue();
//  }
//
//  @Test
//  void transform_withoutContent_shouldReturnSameStatusWithoutContent() {
//    var probable = Probable.value(TEST_VALUE);
//    var actual = Probable.<Integer>transform(probable);
//    ProbableAssertions.assertThat(actual)
//                      .hasValue();
//  }


}
