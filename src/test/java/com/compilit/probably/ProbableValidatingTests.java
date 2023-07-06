package com.compilit.probably;

import static org.assertj.core.api.Assertions.assertThat;

import com.compilit.probably.Probable.Type;
import org.junit.jupiter.api.Test;

class ProbableValidatingTests {

  private static final String TEST_CONTENTS = "value";

  @Test
  void test_valid_shouldReturnProbable() {
    var probable = Probable.value(TEST_CONTENTS);
    assertThat(probable.test(x -> x.equals(TEST_CONTENTS))).isEqualTo(probable);
  }

  @Test
  void test_invalid_shouldReturnAlteredProbable() {
    var probable = Probable.value(TEST_CONTENTS);
    var actual = probable.test(x -> x.equals("something else"));
    assertThat(actual)
      .satisfies(r -> assertThat(r.getType()).isEqualTo(Type.FAILURE))
      .isNotEqualTo(probable);
  }

  @Test
  void test_successfulPredicateTest_shouldReturnProbable() {
    var probable = Probable.value(TEST_CONTENTS).test(x -> x.equals(TEST_CONTENTS));
    assertThat(probable.getType()).isEqualTo(Type.VALUE);
  }

  @Test
  void test_failingPredicateTest_shouldReturnAlteredProbable() {
    var probable = Probable.value(TEST_CONTENTS);
    var actual = probable.test(x -> x.equals("something else"));
    assertThat(actual)
      .satisfies(r -> assertThat(r.getType()).isEqualTo(Type.FAILURE))
      .isNotEqualTo(probable);
  }

}
