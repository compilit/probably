package com.compilit.resultify;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.Function;
import org.junit.jupiter.api.Test;

class MonadicTests {

  @Test
  void leftIdentity() {
    var value = 1;
    var result = Result.success(value);
    Function<Integer, Result<Integer>> f = x -> Result.success(x + 1);
    assertThat(result.flatMap(f)).isEqualTo(f.apply(value));
  }

  @Test
  void rightIdentity() {
    var value = 1;
    var result = Result.success(value);
    Function<Integer, Result<Integer>> f = Result::success;
    assertThat(result.flatMap(f)).isEqualTo(result);
  }

  @Test
  void associativity() {
    var value = 1;
    var result = Result.success(value);
    Function<Integer, Result<Integer>> f = x -> Result.success(x * 2);
    Function<Integer, Result<Integer>> g = x -> Result.success(x + 2);
    var result1 = result.flatMap(f).flatMap(g);
    var result2 = result.flatMap(x -> f.apply(x).flatMap(g));
    assertThat(result1).isEqualTo(result2);

  }
}
