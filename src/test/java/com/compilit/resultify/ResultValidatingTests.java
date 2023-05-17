package com.compilit.resultify;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ResultValidatingTests {

  private static final String TEST_CONTENTS = "contents";

  @Test
  void test_valid_shouldReturnResult() {
    var result = Result.success(TEST_CONTENTS);
    assertThat(result.test(x -> x.equals(TEST_CONTENTS))).isEqualTo(result);
  }

  @Test
  void test_invalid_shouldReturnAlteredResult() {
    var result = Result.success(TEST_CONTENTS);
    var actual = result.test(x -> x.equals("something else"));
    assertThat(actual)
      .satisfies(r -> assertThat(r.getResultType()).isEqualTo(ResultType.UNPROCESSABLE))
      .isNotEqualTo(result);
  }

  @Test
  void test$Unauthorized_valid_shouldReturnResult() {
    var result = Result.success(TEST_CONTENTS);
    assertThat(result.test(x -> x.equals(TEST_CONTENTS), ResultType.UNAUTHORIZED)).isEqualTo(result);
  }

  @Test
  void test$Unauthorized_invalid_shouldReturnAlteredResult() {
    var result = Result.success(TEST_CONTENTS);
    var actual = result.test(x -> x.equals("something else"), ResultType.UNAUTHORIZED);
    assertThat(actual)
      .satisfies(r -> assertThat(r.getResultType()).isEqualTo(ResultType.UNAUTHORIZED))
      .isNotEqualTo(result);
  }

  @Test
  void test$Unprocessable_valid_shouldReturnResult() {
    var result = Result.success(TEST_CONTENTS).test(x -> x.equals(TEST_CONTENTS), ResultType.UNPROCESSABLE);
    assertThat(result.getResultType()).isEqualTo(ResultType.SUCCESS);
  }

  @Test
  void test$Unprocessable_invalid_shouldReturnAlteredResult() {
    var result = Result.success(TEST_CONTENTS);
    var actual = result.test(x -> x.equals("something else"), ResultType.UNPROCESSABLE);
    assertThat(actual)
      .satisfies(r -> assertThat(r.getResultType()).isEqualTo(ResultType.UNPROCESSABLE))
      .isNotEqualTo(result);
  }

}
