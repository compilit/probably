package com.compilit.probably;

import static com.compilit.probably.testutil.TestValue.TEST_CONTENT;
import static org.assertj.core.api.Assertions.assertThat;

import com.compilit.probably.Probable.Type;
import com.compilit.probably.assertions.ProbableAssertions;
import com.compilit.probably.testutil.TestValue;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class ProbableCreationTests {

  @Test
  void successful_shouldReturnSuccessProbable() {
    ProbableAssertions.assertThat(Probable.empty()).isSuccessfulProbable()
                      .isEmpty();
  }

  @Test
  void successful$thenApply_shouldReturnSuccessProbable() {
    ProbableAssertions.assertThat(Probable.empty())
                      .isSuccessfulProbable()
                      .isEmpty();
  }


  @Test
  void successful_shouldReturnSuccessProbableWithContents() {
    ProbableAssertions.assertThat(Probable.value(TEST_CONTENT))
                      .isSuccessfulProbable()
                      .containsContent(TEST_CONTENT);
  }

  @Test
  void failed_shouldReturnFailedProbable() {
    ProbableAssertions.assertThat(Probable.failure(TestValue.TEST_MESSAGE))
                      .isUnsuccessfulProbable()
                      .containsMessage(TestValue.TEST_MESSAGE);
  }

  @Test
  void failed_withFormattedMessage_shouldReturnFormattedMessage() {
    var expected = String.format("test %s", "test");
    var probable = Probable.failure("test %s", "test");
    Assertions.assertThat(probable.getMessage()).isEqualTo(expected);
    Assertions.assertThat(probable.geType()).isEqualTo(Type.EMPTY);
  }

  @Test
  void transform_shouldReturnEmptyProbableWithCorrectStatus() {
    var probable = Probable.value("test");
    assertThat(probable.isEmpty()).isFalse();
    var actual = Probable.<Integer>transform(probable);
    ProbableAssertions.assertThat(actual)
                      .hasContent()
                      .isSuccessfulProbable();
  }

  @Test
  void transform_withoutContent_shouldReturnSameStatusWithoutContent() {
    var probable = Probable.value(TEST_CONTENT);
    var actual = Probable.<Integer>transform(probable);
    ProbableAssertions.assertThat(actual)
                      .hasContent()
                      .isSuccessfulProbable();
  }


}
