package com.compilit.resultify;

import static com.compilit.resultify.Messages.MESSAGE_FORMAT_ERROR;
import static com.compilit.resultify.testutil.TestValue.TEST_CONTENT;
import static org.assertj.core.api.Assertions.assertThat;

import com.compilit.resultify.assertions.ResultAssertions;
import com.compilit.resultify.testutil.TestValue;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class ResultCreationTests {

  @Test
  void success_shouldReturnSuccessResult() {
    ResultAssertions.assertThat(Result.success()).isValidSuccessResult()
                    .isEmpty();
  }

  @Test
  void success$thenApply_shouldReturnSuccessResult() {
    ResultAssertions.assertThat(Result.success())
                    .isValidSuccessResult()
                    .isEmpty();
  }


  @Test
  void success_shouldReturnSuccessResultWithContents() {
    ResultAssertions.assertThat(Result.success(TEST_CONTENT))
                    .isValidSuccessResult()
                    .containsContent(TEST_CONTENT);
  }

  @Test
  void notFound_shouldReturnNotFoundResult() {
    ResultAssertions.assertThat(Result.notFound()).isValidUnsuccessfulResult()
                    .isEmpty();
  }

  @Test
  void notFound_withMessage_shouldReturnUnsuccessfulResult() {
    ResultAssertions.assertThat(Result.notFound(TestValue.TEST_MESSAGE))
                    .isValidUnsuccessfulResult()
                    .containsMessage(TestValue.TEST_MESSAGE);
  }

  @Test
  void notFound_withNullMessage_shouldReturnDefaultMessage() {
    var expected = Messages.NO_MESSAGE_AVAILABLE;
    var result = Result.notFound(null);
    Assertions.assertThat(result.getMessage()).isEqualTo(expected);
    Assertions.assertThat(result.getResultType()).isEqualTo(ResultType.NOT_FOUND);
  }

  @Test
  void notFound_withoutFormatArgs_shouldReturnMessage() {
    var expected = "test";
    var result = Result.notFound(expected);
    Assertions.assertThat(result.getMessage()).isEqualTo(expected);
    Assertions.assertThat(result.getResultType()).isEqualTo(ResultType.NOT_FOUND);
  }

  @Test
  void notFound_withNullFormatArgs_shouldReturnMessage() {
    var expected = "test";
    var result = Result.notFound(expected, null);
    Assertions.assertThat(result.getMessage()).isEqualTo(expected);
    Assertions.assertThat(result.getResultType()).isEqualTo(ResultType.NOT_FOUND);
  }

  @Test
  void notFound_withFormattedMessage_shouldReturnFormattedMessage() {
    var expected = String.format("test %s", "test");
    var result = Result.notFound("test %s", "test");
    Assertions.assertThat(result.getMessage()).isEqualTo(expected);
    Assertions.assertThat(result.getResultType()).isEqualTo(ResultType.NOT_FOUND);
  }

  @Test
  void notFound_withInvalidFormatPlaceholders_shouldReturnMessageContainingExceptionMessage() {
    var message = "test %s, %s";
    var expected = MESSAGE_FORMAT_ERROR;
    var result = Result.notFound(message, "test");
    Assertions.assertThat(result.getMessage()).contains(expected);
    Assertions.assertThat(result.getResultType()).isEqualTo(ResultType.NOT_FOUND);
  }

  @Test
  void unprocessable_shouldReturnUnprocessableResult() {
    ResultAssertions.assertThat(Result.unprocessable(TestValue.TEST_MESSAGE))
                    .isValidUnsuccessfulResult()
                    .containsMessage(TestValue.TEST_MESSAGE);
  }

  @Test
  void unprocessable_withMessage_shouldReturnUnsuccessfulResult() {
    ResultAssertions.assertThat(Result.unprocessable(TestValue.TEST_MESSAGE))
                    .isValidUnsuccessfulResult()
                    .containsMessage(TestValue.TEST_MESSAGE);
  }

  @Test
  void unprocessable_withFormattedMessage_shouldReturnFormattedMessage() {
    var expected = String.format("test %s", "test");
    var result = Result.unprocessable("test %s", "test");
    Assertions.assertThat(result.getMessage()).isEqualTo(expected);
    Assertions.assertThat(result.getResultType()).isEqualTo(ResultType.UNPROCESSABLE);
  }

  @Test
  void unauthorized_withFormattedMessage_shouldReturnFormattedMessage() {
    var expected = String.format("test %s", "test");
    var result = Result.unauthorized("test %s", "test");
    Assertions.assertThat(result.getMessage()).isEqualTo(expected);
    Assertions.assertThat(result.getResultType()).isEqualTo(ResultType.UNAUTHORIZED);
  }

  @Test
  void unauthorized_shouldReturnunauthorizedResult() {
    ResultAssertions.assertThat(Result.unauthorized()).isValidUnsuccessfulResult()
                    .isEmpty();
  }

  @Test
  void unauthorized_withMessage_shouldReturnUnsuccessfulResult() {
    ResultAssertions.assertThat(Result.unauthorized(TestValue.TEST_MESSAGE))
                    .isValidUnsuccessfulResult()
                    .containsMessage(TestValue.TEST_MESSAGE);
  }

  @Test
  void errorOccurred_shouldReturnSuccessResult() {
    ResultAssertions.assertThat(Result.errorOccurred(TestValue.TEST_MESSAGE))
                    .isValidUnsuccessfulResult()
                    .containsMessage(TestValue.TEST_MESSAGE);
  }

  @Test
  void errorOccurred_withFormattedMessage_shouldReturnFormattedMessage() {
    var expected = String.format("test %s", "test");
    var result = Result.errorOccurred("test %s", "test");
    Assertions.assertThat(result.getMessage()).isEqualTo(expected);
    Assertions.assertThat(result.getResultType()).isEqualTo(ResultType.ERROR_OCCURRED);
  }

  @Test
  void transform_shouldReturnEmptyResultWithCorrectStatus() {
    var result = Result.success("test");
    assertThat(result.isEmpty()).isFalse();
    var actual = Result.<Integer>transform(result);
    ResultAssertions.assertThat(actual)
                    .hasContent()
                    .isValidSuccessResult();
  }

  @Test
  void transform_withoutContent_shouldReturnSameStatusWithoutContent() {
    var result = Result.success(TEST_CONTENT);
    var actual = Result.<Integer>transform(result);
    ResultAssertions.assertThat(actual)
                    .hasContent()
                    .isValidSuccessResult();
  }


}
