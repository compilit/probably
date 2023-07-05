package com.compilit.probably;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.Function;
import org.junit.jupiter.api.Test;

class MonadicTests {

  @Test
  void leftIdentity() {
    var value = 1;
    var probable = Probable.value(value);
    Function<Integer, Probable<Integer>> f = x -> Probable.value(x + 1);
    assertThat(probable.flatMap(f)).isEqualTo(f.apply(value));
  }

  @Test
  void rightIdentity() {
    var value = 1;
    var probable = Probable.value(value);
    Function<Integer, Probable<Integer>> f = Probable::value;
    assertThat(probable.flatMap(f)).isEqualTo(probable);
  }

  @Test
  void associativity() {
    var value = 1;
    var probable = Probable.value(value);
    Function<Integer, Probable<Integer>> f = x -> Probable.value(x * 2);
    Function<Integer, Probable<Integer>> g = x -> Probable.value(x + 2);
    var probable1 = probable.flatMap(f).flatMap(g);
    var probable2 = probable.flatMap(x -> f.apply(x).flatMap(g));
    assertThat(probable1).isEqualTo(probable2);

  }

  @Test
  void bla() {
    Function<Integer, Probable<Integer>> f = x -> {
      if (x == null) {
        x = -1;
      } else if (x == 2) {
        x = null;
      } else {
        x = x + 1;
      }
      return Probable.of(x);
    };
    // prints "Optional.empty"
    System.out.println(Probable.of(() -> (Integer) null).flatMap(f));
// prints "Optional[-1]"
    System.out.println(f.apply(null));
  }
}
