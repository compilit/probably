package com.compilit.probably.api;

import static com.compilit.probably.testutil.TestValue.TEST_MESSAGE;
import static com.compilit.probably.testutil.TestValue.TEST_VALUE;

import com.compilit.probably.Probable;
import com.compilit.probably.testutil.ProbableAssertions;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class ApiTest {


  @Test
  void testCase1() {
    var probable = Probable.of(() -> TEST_VALUE)
      .map(v -> v + "yay")
      .map(v -> Probable.of(v + "yay2"))
      .map(v -> Probable.of(v))
      .flatMap(v -> Probable.of(v.get().get() + "yay3"))
      .or(Probable.failure(TEST_MESSAGE));

    ProbableAssertions.assertThat(probable)
      .hasValue();

  }
}
