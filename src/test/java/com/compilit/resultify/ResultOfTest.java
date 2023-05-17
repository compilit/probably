package com.compilit.resultify;

import static com.compilit.resultify.Result.resultOf;
import static com.compilit.resultify.Result.success;

import com.compilit.resultify.assertions.ResultAssertions;
import com.compilit.resultify.testutil.TestValue;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;

class ResultOfTest {

  @Test
  void resultOf_SuccessfulPredicate_shouldReturnSuccessResult() {
    var result = resultOf(x -> true, null);
    ResultAssertions.assertThat(result).isValidSuccessResult()
                    .isEmpty();
  }

  @Test
  void resultOf_UnsuccessfulPredicate_shouldReturnUnprocessableResult() {
    var result = resultOf(x -> false, null);
    ResultAssertions.assertThat(result).isValidUnsuccessfulResult()
                    .isEmpty();
  }

  @Test
  void resultOf_ExceptionalPredicate_shouldReturnErrorOccurredResult() {
    var exception = new RuntimeException(TestValue.TEST_CONTENT);
    var throwingPredicate = new Predicate<String>() {
      @Override
      public boolean test(String s) {
        throw exception;
      }
    };
    var message = exception.getMessage();
    var result = resultOf(throwingPredicate, null);
    ResultAssertions.assertThat(result).isValidUnsuccessfulResult().containsMessage(message);
  }

  @Test
  void resultOf_successfulSupplier_shouldReturnSuccessResult() {
    Supplier<String> supplier = () -> TestValue.TEST_CONTENT;
    var result = resultOf(supplier);
    ResultAssertions.assertThat(result)
                    .isValidSuccessResult()
                    .containsContent(TestValue.TEST_CONTENT);
  }

  @Test
  void resultOf_UnsuccessfulSupplier_shouldReturnErrorOccurredResult() {
    var exception = new RuntimeException(TestValue.TEST_CONTENT);
    var Supplier = new Supplier<Result<String>>() {
      @Override
      public Result<String> get() {
        throw exception;
      }
    };
    var message = exception.getMessage();
    var result = resultOf(Supplier);
    ResultAssertions.assertThat(result).isValidUnsuccessfulResult().containsMessage(message);
  }

  @Test
  void resultOf_nested_shouldReturnSuccessResult() {
    var result = resultOf(r -> success(123)
      .flatMap(x -> success(String.valueOf(x))
        .flatMap(z -> success(Integer.valueOf(z)))));
    ResultAssertions.assertThat(result)
                    .isValidSuccessResult()
                    .containsContent(123);
  }

}
