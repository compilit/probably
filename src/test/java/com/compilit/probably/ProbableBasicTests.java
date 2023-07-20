package com.compilit.probably;

import static com.compilit.probably.testutil.TestValue.TEST_VALUE;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ProbableBasicTests {

  @Test
  void equals_true_shouldReturnTrue() {
    assertEquals(Probable.of(TEST_VALUE), Probable.of(TEST_VALUE));
    var valueEquality = Probable.of(TEST_VALUE).equals(TEST_VALUE);
    assertTrue(valueEquality);
    var nullEquality = Probable.of(null).equals(null);
    assertTrue(nullEquality);
  }

  @Test
  void equals_false_shouldReturnFalse() {
    assertNotEquals(Probable.of(TEST_VALUE), Probable.of(TEST_VALUE + TEST_VALUE));
  }

  @Test
  void equals_null_shouldReturnFalse() {
    assertNotEquals(null, Probable.of(TEST_VALUE));
  }

  @Test
  void equals_differentObject_shouldReturnFalse() {
    assertNotEquals(new Object(), Probable.of(TEST_VALUE));
  }

  @Test
  void hashcode_null_shouldReturnZero() {
    assertThat(Probable.of(null).hashCode()).isZero();
  }

  @Test
  void hashcode_string_shouldReturnHashcodeOfValue() {
    assertThat(Probable.of(TEST_VALUE)).hasSameHashCodeAs(TEST_VALUE);
  }
}
