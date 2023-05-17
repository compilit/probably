package com.compilit.resultify;

import static com.compilit.resultify.Messages.NOTHING_TO_REPORT;
import static com.compilit.resultify.testutil.TestValue.TEST_CONTENT;
import static com.compilit.resultify.testutil.TestValue.TEST_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.compilit.resultify.testutil.TestValue;
import org.junit.jupiter.api.Test;

class ResultMethodTests {

  @Test
  void isEmpty_emptyResult_shouldReturnTrue() {
    var result = Result.success();
    assertThat(result.isEmpty()).isTrue();
  }

  @Test
  void isEmpty_filledResult_shouldReturnFalse() {
    var result = Result.success("test");
    assertThat(result.isEmpty()).isFalse();
  }

  @Test
  void getResultType_shouldReturnResultType() {
    assertThat(Result.success().getResultType()).isEqualTo(ResultType.SUCCESS);
    assertThat(Result.errorOccurred(TEST_MESSAGE).getResultType())
      .isEqualTo(ResultType.ERROR_OCCURRED);
    assertThat(Result.success().getResultType())
      .isEqualTo(ResultType.SUCCESS);
    assertThat(Result.notFound().getResultType()).isEqualTo(ResultType.NOT_FOUND);
    assertThat(Result.unauthorized().getResultType())
      .isEqualTo(ResultType.UNAUTHORIZED);
    assertThat(Result.unprocessable().getResultType())
      .isEqualTo(ResultType.UNPROCESSABLE);
  }

  @Test
  void hasContents_withContents_shouldReturnTrue() {
    var result = Result.success(TestValue.TEST_CONTENT);
    assertThat(result.hasContents()).isTrue();
  }

  @Test
  void hasContents_withoutContents_shouldReturnFalse() {
    var result = Result.success();
    assertThat(result.hasContents()).isFalse();
  }

  @Test
  void isEmpty_withContents_shouldReturnFalse() {
    var result = Result.success(TestValue.TEST_CONTENT);
    assertThat(result.isEmpty()).isFalse();
  }

  @Test
  void isEmpty_withoutContents_shouldReturnTrue() {
    var result = Result.success();
    assertThat(result.isEmpty()).isTrue();
  }

  @Test
  void getOptionalContents_shouldReturnContentsAsOptional() {
    var result = Result.success(TestValue.TEST_CONTENT);
    assertThat(result.get()).contains(TestValue.TEST_CONTENT);
  }

  @Test
  void getContents_shouldReturnContents() {
    var result = Result.success(TestValue.TEST_CONTENT);
    assertThat(result.get()).isEqualTo(TestValue.TEST_CONTENT);
  }
  @Test
  void thenApply_intResultToString_shouldReturnString() {
    var input = 123;
    var expected = "123";
    var result = Result.success(input);
    var actual = result.map(String::valueOf);
    assertThat(actual.get()).isEqualTo(expected);
  }

  @Test
  void thenApply_success_shouldApplyFunction() {
    var expected = "10";
    var result = Result.success(10);
    Result<String> actual = result.map(String::valueOf);
    assertThat(actual.get()).contains(expected);
  }

  @Test
  void thenApply_errorOccurred_shouldReturnResultWithMessage() {
    var result = Result.errorOccurred(TEST_MESSAGE);
    var actual = result.map(x -> TEST_CONTENT);
    assertThat(actual.getMessage()).isEqualTo(TEST_MESSAGE);
  }
  @Test
  void getNullableContents_null_shouldReturnNull() {
    var errorResult = Result.errorOccurred(TEST_MESSAGE);
    assertThat(errorResult.get()).isNull();
  }

  @Test
  void getMessage_shouldReturnMessage() {
    var errorMessage = "I am error";
    var errorResult = Result.errorOccurred(errorMessage);
    assertThat(errorResult.getMessage()).isEqualTo(errorMessage);
    var successResult = Result.success();
    assertThat(successResult.getMessage()).isEqualTo(NOTHING_TO_REPORT);
  }

  @Test
  void getMessage_withFormattedMessage_shouldReturnFormattedMessage() {
    var expected = String.format("I am error %s %s %s %s", "test1", "test2", "someValue", "test3");
    var actual = Result.errorOccurred("I am error %s %s %s %s", "test1", "test2", "someValue", "test3");
    assertThat(actual.getMessage()).isEqualTo(expected);
  }

}
