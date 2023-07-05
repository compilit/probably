package com.compilit.probably;

import static com.compilit.probably.Probable.of;

import com.compilit.probably.assertions.ProbableAssertions;
import com.compilit.probably.testutil.TestValue;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;

class ProbableOfTest {

  @Test
  void probableOf_SuccessfulPredicate_shouldReturnSuccessProbable() {
    var probable = of(x -> true, null);
    ProbableAssertions.assertThat(probable).isSuccessfulProbable()
                      .isEmpty();
  }

  @Test
  void probableOf_UnsuccessfulPredicate_shouldReturnUnprocessableProbable() {
    var probable = of(x -> false, null);
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
    var probable = of(throwingPredicate, null);
    ProbableAssertions.assertThat(probable).isUnsuccessfulProbable().containsMessage(message);
  }

  @Test
  void probableOf_successfulSupplier_shouldReturnSuccessProbable() {
    Supplier<String> supplier = () -> TestValue.TEST_CONTENT;
    var probable = Probable.of(supplier);
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
    var probable = Probable.of(Supplier);
    ProbableAssertions.assertThat(probable).isUnsuccessfulProbable().containsMessage(message);
  }

  @Test
  void probableOf_nested_shouldReturnSuccessProbable() {
    var probable = Probable.of(r -> Probable.value(123)
                                            .flatMap(x -> Probable.value(String.valueOf(x))
                                                                  .flatMap(z -> Probable.value(Integer.valueOf(z)))));
    ProbableAssertions.assertThat(probable)
                      .isSuccessfulProbable()
                      .containsContent(123);
  }

}
