package com.compilit.probably;

import static com.compilit.probably.Probable.probableOf;
import static com.compilit.probably.Probable.successful;

import com.compilit.probably.assertions.ProbableAssertions;
import com.compilit.probably.testutil.TestValue;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;

class ProbableOfTest {

  @Test
  void probableOf_SuccessfulPredicate_shouldReturnSuccessProbable() {
    var probable = probableOf(x -> true, null);
    ProbableAssertions.assertThat(probable).isSuccessfulProbable()
                      .isEmpty();
  }

  @Test
  void probableOf_UnsuccessfulPredicate_shouldReturnUnprocessableProbable() {
    var probable = probableOf(x -> false, null);
    ProbableAssertions.assertThat(probable).isUnsuccessfulProbable()
                      .isEmpty();
  }

  @Test
  void probableOf_ExceptionalPredicate_shouldReturnUnsuccessfulProbable() {
    var exception = new RuntimeException(TestValue.TEST_CONTENT);
    var throwingPredicate = new Predicate<String>() {
      @Override
      public boolean test(String s) {
        throw exception;
      }
    };
    var message = exception.getMessage();
    var probable = probableOf(throwingPredicate, null);
    ProbableAssertions.assertThat(probable).isUnsuccessfulProbable().containsMessage(message);
  }

  @Test
  void probableOf_successfulSupplier_shouldReturnSuccessProbable() {
    Supplier<String> supplier = () -> TestValue.TEST_CONTENT;
    var probable = probableOf(supplier);
    ProbableAssertions.assertThat(probable)
                      .isSuccessfulProbable()
                      .containsContent(TestValue.TEST_CONTENT);
  }

  @Test
  void probableOf_UnsuccessfulSupplier_shouldReturnUnsuccessfulProbable() {
    var exception = new RuntimeException(TestValue.TEST_CONTENT);
    var Supplier = new Supplier<Probable<String>>() {
      @Override
      public Probable<String> get() {
        throw exception;
      }
    };
    var message = exception.getMessage();
    var probable = probableOf(Supplier);
    ProbableAssertions.assertThat(probable).isUnsuccessfulProbable().containsMessage(message);
  }

  @Test
  void probableOf_nested_shouldReturnSuccessProbable() {
    var probable = probableOf(r -> successful(123)
      .flatMap(x -> successful(String.valueOf(x))
        .flatMap(z -> successful(Integer.valueOf(z)))));
    ProbableAssertions.assertThat(probable)
                      .isSuccessfulProbable()
                      .containsContent(123);
  }

}
