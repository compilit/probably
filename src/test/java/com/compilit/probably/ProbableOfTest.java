package com.compilit.probably;

import static com.compilit.probably.Probable.of;

import com.compilit.probably.assertions.ProbableAssertions;
import com.compilit.probably.testutil.TestValue;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;

class ProbableOfTest {

  @Test
  void probableOf_successfulPredicate_shouldReturnSuccessProbable() {
    var probable = of(x -> true, null);
    ProbableAssertions.assertThat(probable)
                      .isEmpty();
  }

  @Test
  void probableOf_unsuccessfulPredicate_shouldReturnUnprocessableProbable() {
    var probable = of(x -> false, null);
    ProbableAssertions.assertThat(probable).hasFailed()
                      .isEmpty();
  }

  @Test
  void probableOf_exceptionalPredicate_shouldReturnUnsuccessfulProbable() {
    var exception = new RuntimeException(TestValue.TEST_MESSAGE);
    var throwingPredicate = new Predicate<String>() {
      @Override
      public boolean test(String s) {
        throw exception;
      }
    };
    var message = exception.getMessage();
    var probable = of(throwingPredicate, null);
    ProbableAssertions.assertThat(probable).hasFailed().containsMessage(message);
  }

  @Test
  void probableOf_successfulSupplier_shouldReturnSuccessProbable() {
    Supplier<String> supplier = () -> TestValue.TEST_VALUE;
    var probable = Probable.of(supplier);
    ProbableAssertions.assertThat(probable)
                      .hasValue(TestValue.TEST_VALUE);
  }

  @Test
  void probableOf_unsuccessfulSupplier_shouldReturnUnsuccessfulProbable() {
    var exception = new RuntimeException(TestValue.TEST_VALUE);
    var Supplier = new Supplier<Probable<String>>() {
      @Override
      public Probable<String> get() {
        throw exception;
      }
    };
    var message = exception.getMessage();
    var probable = Probable.of(Supplier);
    ProbableAssertions.assertThat(probable).hasFailed().containsMessage(message);
  }

  @Test
  void probableOf_nested_shouldReturnSuccessProbable() {
    var probable = Probable.of(r -> Probable.value(123)
                                            .flatMap(x -> Probable.value(String.valueOf(x))
                                                                  .flatMap(z -> Probable.value(Integer.valueOf(z)))));
    ProbableAssertions.assertThat(probable)
                      .hasValue(123);
  }

}
