package com.compilit.probably;

import static com.compilit.probably.Messages.NOTHING_TO_REPORT;
import static com.compilit.probably.testutil.TestValue.TEST_VALUE;
import static com.compilit.probably.testutil.TestValue.TEST_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.compilit.probably.testutil.MemoryAppender;
import com.compilit.probably.testutil.ProbableAssertions;
import com.compilit.probably.testutil.TestValue;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

class ProbableMethodTests {

  private static final String TEST_CONTENTS = "value";
  @Test
  void isEmpty_emptyProbable_shouldReturnTrue() {
    var probable = Probable.nothing();
    assertThat(probable.isEmpty()).isTrue();
  }

  @Test
  void isEmpty_filledProbable_shouldReturnFalse() {
    var probable = Probable.of("test");
    assertThat(probable.isEmpty()).isFalse();
  }

  @Test
  void hasFailed_true_shouldReturnTrue() {
    assertThat(Probable.failure(TEST_MESSAGE).hasFailed()).isTrue();
  }

  @Test
  void hasFailed_false_shouldReturnFalse() {
    assertThat(Probable.nothing().hasFailed()).isFalse();
    assertThat(Probable.of(TEST_VALUE).hasFailed()).isFalse();
  }

  @Test
  void hasValue_withContents_shouldReturnTrue() {
    var probable = Probable.of(TestValue.TEST_VALUE);
    assertThat(probable.hasValue()).isTrue();
  }

  @Test
  void hasValue_withoutContents_shouldReturnFalse() {
    var probable = Probable.nothing();
    assertThat(probable.hasValue()).isFalse();
  }

  @Test
  void isEmpty_withContents_shouldReturnFalse() {
    var probable = Probable.of(TestValue.TEST_VALUE);
    assertThat(probable.isEmpty()).isFalse();
  }

  @Test
  void isEmpty_withoutContents_shouldReturnTrue() {
    var probable = Probable.nothing();
    assertThat(probable.isEmpty()).isTrue();
  }

  @Test
  void getValue_shouldReturnContentsAsOptional() {
    var probable = Probable.of(TestValue.TEST_VALUE);
    assertThat(probable.getValue()).contains(TestValue.TEST_VALUE);
  }

  @Test
  void getValue_shouldReturnContents() {
    var probable = Probable.of(TestValue.TEST_VALUE);
    assertThat(probable.getValue()).isEqualTo(TestValue.TEST_VALUE);
  }

  @Test
  void getOptionalValue_shouldReturnContentsAsOptional() {
    var probable = Probable.of(TestValue.TEST_VALUE);
    assertThat(probable.getOptionalValue()).contains(TestValue.TEST_VALUE);
  }

  @Test
  void getOptionalValue_shouldReturnContents() {
    var probable = Probable.of(TestValue.TEST_VALUE);
    assertThat(probable.getOptionalValue()).contains(TestValue.TEST_VALUE);
  }


  @Test
  void orElse_hasValue_shouldReturnValue() {
    assertThat(Probable.of(TEST_VALUE).orElse(TEST_CONTENTS)).isEqualTo(TEST_VALUE);
  }

  @Test
  void orElse_hasFailed_shouldReturnOtherValue() {
    assertThat(Probable.failure(TEST_MESSAGE).orElse(TEST_VALUE)).isEqualTo(TEST_VALUE);
  }

  @Test
  void orElse_isEmpty_shouldReturnOtherValue() {
    assertThat(Probable.nothing().orElse(TEST_VALUE)).isEqualTo(TEST_VALUE);
  }

  @Test
  void orElse$Supplier_hasValue_shouldReturnValue() {
    assertThat(Probable.of(TEST_VALUE).orElse(() -> TEST_CONTENTS)).isEqualTo(TEST_VALUE);
  }

  @Test
  void orElse$Supplier_hasFailed_shouldReturnOtherValue() {
    assertThat(Probable.failure(TEST_MESSAGE).orElse(() -> TEST_VALUE)).isEqualTo(TEST_VALUE);
  }

  @Test
  void orElse$Supplier_isEmpty_shouldReturnOtherValue() {
    assertThat(Probable.nothing().orElse(() -> TEST_VALUE)).isEqualTo(TEST_VALUE);
  }

  @Test
  void orElse$Supplier_throwsException_shouldReturnOtherValue() {
    assertThat(Probable.nothing().orElse(() -> TEST_VALUE)).isEqualTo(TEST_VALUE);
  }

  @Test
  void test_valid_shouldReturnProbable() {
    var probable = Probable.of(TEST_CONTENTS);
    assertThat(probable.test(x -> x.equals(TEST_CONTENTS))).isEqualTo(probable);
  }

  @Test
  void test_invalid_shouldReturnAlteredProbable() {
    var probable = Probable.of(TEST_CONTENTS);
    var actual = probable.test(x -> x.equals("something else"));
    assertThat(actual)
      .satisfies(r -> ProbableAssertions.assertThat(r).hasFailed())
      .isNotEqualTo(probable);
  }

  @Test
  void test_successfulPredicateTest_shouldReturnProbable() {
    var probable = Probable.of(TEST_CONTENTS).test(x -> x.equals(TEST_CONTENTS));
    ProbableAssertions.assertThat(probable).hasValue();
  }

  @Test
  void test_failingPredicateTest_shouldReturnAlteredProbable() {
    var probable = Probable.of(TEST_CONTENTS);
    var actual = probable.test(x -> x.equals("something else"));
    assertThat(actual)
      .satisfies(r -> ProbableAssertions.assertThat(r).hasFailed())
      .isNotEqualTo(probable);
  }

  @Test
  void test_exception_shouldReturnProbableFailure() {
    var probable = Probable.of(TEST_CONTENTS);
    var actual = probable.test(x -> {throw new RuntimeException();});
    assertThat(actual)
      .satisfies(r -> ProbableAssertions.assertThat(r).hasFailed())
      .isNotEqualTo(probable);
  }

  @Test
  void map_intProbableToString_shouldReturnString() {
    var input = 123;
    var expected = "123";
    var probable = Probable.of(input);
    var actual = probable.map(String::valueOf);
    assertThat(actual.getValue()).isEqualTo(expected);
  }

  @Test
  void map_value_shouldApplyFunction() {
    var expected = "10";
    var probable = Probable.of(10);
    Probable<String> actual = probable.map(String::valueOf);
    assertThat(actual.getValue()).contains(expected);
  }

  @Test
  void map_failure_shouldReturnProbableWithMessage() {
    var probable = Probable.failure(TEST_MESSAGE);
    var actual = probable.map(x -> TEST_VALUE);
    assertThat(actual.getMessage()).isEqualTo(TEST_MESSAGE);
  }

  @Test
  void map_null_shouldReturnProbableNothing() {
    var probable = Probable.of(null);
    var actual = probable.map(x -> TEST_VALUE);
    ProbableAssertions.assertThat(actual).isEmpty();
  }

  @Test
  void map_exception_shouldReturnProbableFailure() {
    var probable = Probable.of(TEST_VALUE);
    var actual = probable.map(x -> {throw new RuntimeException();});
    ProbableAssertions.assertThat(actual).hasFailed();
  }

  @Test
  void flatMap_intProbableToString_shouldReturnProbableValue() {
    var probable = Probable.of(1);
    var actual = probable.flatMap(x -> Probable.of(x.toString()));
    ProbableAssertions.assertThat(actual).hasValue("1");
  }

  @Test
  void flatMap_null_shouldReturnProbableNothing() {
    var probable = Probable.of(null);
    var actual = probable.flatMap(x -> Probable.of(TEST_VALUE));
    ProbableAssertions.assertThat(actual).isEmpty();
  }

  @Test
  void flatMap_exception_shouldReturnProbableFailure() {
    var probable = Probable.of(TEST_VALUE);
    var actual = probable.flatMap(x -> {throw new RuntimeException();});
    ProbableAssertions.assertThat(actual).hasFailed();
  }

  @Test
  void getValue_null_shouldReturnNull() {
    var errorProbable = Probable.failure(TEST_MESSAGE);
    assertThat(errorProbable.getValue()).isNull();
  }

  @Test
  void getMessage_shouldReturnMessage() {
    var errorMessage = "I am error";
    var errorProbable = Probable.failure(errorMessage);
    assertThat(errorProbable.getMessage()).isEqualTo(errorMessage);
    var successfulProbable = Probable.nothing();
    assertThat(successfulProbable.getMessage()).isEqualTo(NOTHING_TO_REPORT);
  }

  @Test
  void getMessage_withFormattedMessage_shouldReturnFormattedMessage() {
    var expected = String.format("I am error %s %s %s %s", "test1", "test2", "someValue", "test3");
    var actual = Probable.failure("I am error %s %s %s %s", "test1", "test2", "someValue", "test3");
    assertThat(actual.getMessage()).isEqualTo(expected);
  }

  @Test
  void or_onValue_shouldReturnTheOriginalProbable() {
    var probable = Probable.of(TEST_VALUE);
    assertThat(probable.or(Probable.nothing())).isEqualTo(probable);
  }

  @Test
  void or_onNothing_shouldReturnTheOtherProbable() {
    var probable = Probable.<String>nothing();
    var otherProbable = Probable.of(TEST_VALUE);
    assertThat(probable.or(otherProbable)).isEqualTo(otherProbable);
  }

  @Test
  void or_onFailure_shouldReturnTheOtherProbable() {
    var probable = Probable.<String>failure(TEST_MESSAGE);
    var otherProbable = Probable.of(TEST_VALUE);
    assertThat(probable.or(otherProbable)).isEqualTo(otherProbable);
  }

  @Test
  void log_shouldBeIdemPotent() {
    var initial = Probable.of(TEST_VALUE);
    var returnType = initial.log(TEST_MESSAGE + 1).log(TEST_MESSAGE + 2).log(TEST_MESSAGE + 3);
    assertThat(returnType).isEqualTo(initial);
  }

  @Test
  void log_debugOn_shouldLogDebugEvents() {
    Logger logger = (Logger) LoggerFactory.getLogger(Probable.class);
    var memoryAppender = new MemoryAppender();
    memoryAppender.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
    logger.setLevel(Level.DEBUG);
    logger.addAppender(memoryAppender);
    memoryAppender.start();
    var initial = Probable.of(TEST_VALUE);
    var returnType = initial.map(x -> x + TEST_VALUE)
           .flatMap(Probable::of)
           .test(x -> x.contains(TEST_VALUE))
           .log(TEST_MESSAGE);
    assertThat(returnType).isNotEqualTo(initial);
    assertThat(memoryAppender.countEventsForLogger("com.compilit.probably.Probable")).isEqualTo(4);
  }

  @Test
  void log_debugOff_shouldNotLogDebugEvents() {
    Logger logger = (Logger) LoggerFactory.getLogger(Probable.class);
    var memoryAppender = new MemoryAppender();
    memoryAppender.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
    logger.setLevel(Level.INFO);
    logger.addAppender(memoryAppender);
    memoryAppender.start();
    var initial = Probable.of(TEST_VALUE);
    var returnType = initial.map(x -> x + TEST_VALUE)
           .flatMap(Probable::of)
           .test(x -> x.contains(TEST_VALUE))
           .log();
    assertThat(returnType).isNotEqualTo(initial);
    assertThat(memoryAppender.countEventsForLogger("com.compilit.probably.Probable")).isEqualTo(1);
  }

  @Test
  void log$messageWithArgs_debugOn_shouldLogDebugEvents() {
    Logger logger = (Logger) LoggerFactory.getLogger(Probable.class);
    var memoryAppender = new MemoryAppender();
    memoryAppender.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
    logger.setLevel(Level.DEBUG);
    logger.addAppender(memoryAppender);
    memoryAppender.start();
    var initial = Probable.of(TEST_VALUE);
    var returnType = initial.map(x -> x + TEST_VALUE)
           .flatMap(Probable::of)
           .test(x -> x.contains(TEST_VALUE))
           .log("test {}", "123");
    assertThat(returnType).isNotEqualTo(initial);
    assertThat(memoryAppender.contains("test 123", Level.INFO)).isTrue();
    assertThat(memoryAppender.countEventsForLogger("com.compilit.probably.Probable")).isEqualTo(4);
  }

  @Test
  void log$messageWithArgs_debugOff_shouldNotLogDebugEvents() {
    Logger logger = (Logger) LoggerFactory.getLogger(Probable.class);
    var memoryAppender = new MemoryAppender();
    memoryAppender.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
    logger.setLevel(Level.INFO);
    logger.addAppender(memoryAppender);
    memoryAppender.start();
    var initial = Probable.of(TEST_VALUE);
    var returnType = initial.map(x -> x + TEST_VALUE)
           .flatMap(Probable::of)
           .test(x -> x.contains(TEST_VALUE))
           .log("test {}", "123");
    assertThat(returnType).isNotEqualTo(initial);
    assertThat(memoryAppender.contains("test 123", Level.INFO)).isTrue();
    assertThat(memoryAppender.countEventsForLogger("com.compilit.probably.Probable")).isEqualTo(1);
  }

  @Test
  void logFailure_failure_shouldLog() {
    Logger logger = (Logger) LoggerFactory.getLogger(Probable.class);
    var memoryAppender = new MemoryAppender();
    memoryAppender.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
    logger.setLevel(Level.INFO);
    logger.addAppender(memoryAppender);
    memoryAppender.start();
    var testMessage = "something has gone wrong";
    var probable = Probable.failure(TEST_MESSAGE);
    var result = probable.logFailure(testMessage);
    assertEquals(result, probable);
    assertThat(memoryAppender.contains(testMessage, Level.ERROR)).isTrue();
  }

  @Test
  void logFailure_noFailure_shouldNotLog() {
    Logger logger = (Logger) LoggerFactory.getLogger(Probable.class);
    var memoryAppender = new MemoryAppender();
    memoryAppender.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
    logger.setLevel(Level.INFO);
    logger.addAppender(memoryAppender);
    memoryAppender.start();
    var probable = Probable.of(TEST_MESSAGE);
    var result = probable.logFailure("testMessage");
    assertEquals(result, probable);
    assertThat(memoryAppender.countEventsForLogger("com.compilit.probably.Probable")).isZero();
  }

  @Test
  void then$Consumer_valuePresent_shouldApplyConsumer() {
    AtomicReference<Boolean> atomicReference = new AtomicReference<>(false);
    var probable = Probable.of(TEST_VALUE);
    var result = probable.thenAccept(x -> atomicReference.set(true));
    assertThat(atomicReference.get()).isTrue();
    assertEquals(probable, result);
  }
  @Test
  void then$Consumer_noValuePresent_shouldNotApplyConsumer() {
    AtomicReference<Boolean> atomicReference = new AtomicReference<>(false);
    var probable = Probable.of(null);
    var result = probable.thenAccept(x -> atomicReference.set(true));
    assertThat(atomicReference.get()).isFalse();
    assertEquals(probable, result);
  }

  @Test
  void then$Runnable_valuePresent_shouldRunRunnable() {
    AtomicReference<Boolean> atomicReference = new AtomicReference<>(false);
    var probable = Probable.of(TEST_VALUE);
    var result = probable.thenRun(() -> atomicReference.set(true));
    assertThat(atomicReference.get()).isTrue();
    assertEquals(probable, result);
  }
  @Test
  void then$Runnable_notFailed_shouldRunRunnable() {
    AtomicReference<Boolean> atomicReference = new AtomicReference<>(false);
    var probable = Probable.of(null);
    var result = probable.thenRun(() -> atomicReference.set(true));
    assertThat(atomicReference.get()).isTrue();
    assertEquals(probable, result);
  }

  @Test
  void then$Runnable_failure_shouldNotRunRunnable() {
    AtomicReference<Boolean> atomicReference = new AtomicReference<>(false);
    var probable = Probable.failure(TEST_MESSAGE);
    var result = probable.thenRun(() -> atomicReference.set(true));
    assertThat(atomicReference.get()).isFalse();
    assertEquals(probable, result);
  }

  @Test
  void stream_withValue_shouldReturnStreamOfValue() {
    var probable = Probable.of(TEST_VALUE);
    var allMatch = probable.stream().allMatch(x -> x.equals(TEST_VALUE));
    assertTrue(allMatch);
  }

  @Test
  void stream_noValue_shouldReturnEmptyStream() {
    var probable = Probable.of(null);
    var stream = probable.stream();
    assertThat(stream).isEmpty();
  }

  @Test
  void getDeepestNestedProbable_firstLevel_shouldReturnSelf() {
    var probable = Probable.of(TEST_VALUE);
    assertThat(new InternalProbable<>(probable).getDeepestNestedProbable()).isEqualTo(probable);
  }

  @Test
  void getDeepestNestedProbable_nested_shouldReturnDeepestLevel() {
    var probable3 = Probable.of(TEST_VALUE);
    var probable2 = Probable.of(probable3);
    var probable1 = Probable.of(probable2);
    assertThat(new InternalProbable<>(probable1).getDeepestNestedProbable()).isEqualTo(probable3);
  }

  @Test
  void getDeepestNestedProbable_failed_shouldReturnProbableFailed() {
    var probable = Probable.failure(TEST_MESSAGE);
    assertThat(new InternalProbable<>(probable).getDeepestNestedProbable()).isEqualTo(probable);
  }

  @Test
  void getDeepestNestedProbable_nothing_shouldReturnProbableNothing() {
    var probable = Probable.nothing();
    assertThat(new InternalProbable<>(probable).getDeepestNestedProbable()).isEqualTo(probable);
  }

  @Test
  void or$Supplier_ProbableValue_shouldReturnOriginal() {
    var probable = Probable.of(TEST_VALUE);
    var otherProbable = Probable.of("something else");
    assertThat(probable.or(() -> otherProbable)).isEqualTo(probable);
  }

  @Test
  void or$Supplier_ProbableFailure_shouldReturnOther() {
    var probable = Probable.<String>failure(TEST_MESSAGE);
    var otherProbable = Probable.of("something else");
    assertThat(probable.or(() -> otherProbable)).isEqualTo(otherProbable);
  }
}
