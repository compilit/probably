# Resultify

Simple monad library to encapsulate and propagate processing results.

Often when something deep in our code goes wrong, we have only our exceptions to rely on propagating
error messages. But what if what happens isn't an actual "exception"? Exceptions should be just that. Exceptional. For
everything else a simple Result will suffice. Using results also enables you to better avoid using exceptions as a
control flow mechanism (which is an anti-pattern).

Don't confuse server responses with Results. A 404 response can be wrapped in a "not found" result,
but a "not found" result does not necessarily mean that somewhere a server gave you a 404 server response. This means
that it not always straightforward to map a Result to a server response, be cautious.

# installation

Get this dependency with the latest version

```xml

<dependency>
  <artifactId>resultify</artifactId>
  <groupId>com.compilit</groupId>
</dependency>
```

# usage

Everything can be handled through the Result interface. Whenever you have some process that could
possibly fail, make sure that it returns a Result. Which Result should be returned can be chosen manually or by passing
the process as a function into the resultOf methods.

```java
import com.compilit.resultify.Result;

class Example {

  Result<?> exampleMethod1() {
    if (everythingWentWellInAVoidProcess()) {
      return Result.success();
    } else {
      return Result.errorOccurred(TEST_MESSAGE);
    }
  }

  Result<?> exampleMethod2() {
    if (everythingWentWellInAProcess()) {
      return Result.success(content);
    } else {
      return Result.errorOccurred(TEST_MESSAGE);
    }
  }

  Result<?> exampleMethod3() {
    if (something.doesNotMeetOurExpectations()) {
      return Result.unprocessable("Reason");
    } else {
      return Result.errorOccurred(TEST_MESSAGE);
    }
  }

  Result<?> exampleMethod4() {
    return Result.resultOf(() -> doSomethingDangerous());
  }

  Result<?> exampleMethod5() {
    return Result.<SomeOtherType>transform(Result.<OneType>errorOccured()); // Returns the error result, with the matching return type.
  }
}

```

### Chaining results

The map and flatMap methods enable you to take your result and apply a function to it. But only in case
it is successful. This means you can chain result methods through a fluent API. Caution should be taken when handling a
successful result, since results can be successful but not have any contents. Here is an example:

```java
class ExampleClass {
  //(...)

  Result<String> getResult(Long id) {
    return respository.findById(id)
                      .test(entity -> entity.isValid()) // will do nothing if the result is already unsuccessful, will do nothing if the predicate returns true, otherwise mutate the result into Unprocessable
                      .map(entity -> entity.getName()); //change the contents of the result if the result is successful. This in term will yield another result.
  }
}
```

Here we call some repository and transform the result into a String, but first we test if the entity is in fact valid.
If the result was not successful the original result will be returned without content.

### map vs flatMap

For those who don't know when to use which, map is the default method used to chain operations in case you don't have any nested
results. But if you do have nested results, use the flatMap method. This avoids having to deal with results like Result<
Result<String>> and instead transforms it into a Result<String>.
