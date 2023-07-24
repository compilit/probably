# Probably

Simple monad library to encapsulate and propagate processing results.

Often when something deep in our code goes wrong, we have only our exceptions to rely on propagating
error messages. But what if what happens isn't an actual "exception"? Exceptions should be just that. Exceptional. For
everything else a simple Probable will suffice. Using Probables enables you to better avoid using exceptions as a
control flow mechanism (which is an anti-pattern).

A Probable can be an instance of one of three possible subtypes:

- <b>Probable.Value</b> - which means that the process it encapsulated was successful and yielded a value
- <b>Probable.Nothing</b>  - which means that the process it encapsulated was successful but did not yield a value (this
  must've
  been intentional).
- <b>Probable.Failure</b>  - which means that the process encountered an error or some validation upon the value did not
  pass

All Probables will have a default message, but a custom message can always be added to provide more information about
the outcome. These messages can later be used in logging messages for example. Or directly by using the provided `log()`
methods.

### installation

Get this dependency with the latest version

```xml

<dependency>
  <artifactId>probably</artifactId>
  <groupId>com.compilit</groupId>
</dependency>
```

### usage

Everything can be handled through the Probable class. Whenever you have some process that could
possibly fail, make sure that it returns a Probable. Which Probable should be returned can be chosen manually or by
passing the process as a Suppler into the `Probable.of(Supplier<T> supplier);` function

```java


class Example {

  Probable<Void> exampleMethod1() {
    if (everythingWentWellInAVoidProcess()) {
      return Probable.nothing();
    } else {
      return Probable.failure("Reason message %s", someValue);
    }
  }

  Probable<String> exampleMethod2() {
    if (everythingWentWellInAProcess()) {
      return Probable.value(content);
    } else {
      return Probable.failure(TEST_MESSAGE);
    }
  }

  Probable<String> exampleMethod3() {
    return Probable.of(() -> doSomethingDangerous());
  }

}

```

### Chaining probables

Methods like map, flatMap, test, thenRun and thenApply enable you to take your probable and apply a function to it. But only in case
it has a value. This means you can chain probable methods through a fluent API. Map and flatMap won't do anything to a
null value (in other words, a Probable.Nothing or Probable.Failure). Otherwise, your original, possibly failed
Probable would be lost by the Probable encountering exceptions during the map and flatMap methods. Here is an example:

```java
class ExampleClass {
  //(...)

  Probable<String> getProbable(Long id) {
    return respository.findById(id)
                      .test(entity -> entity.canBeAccessed()) // will do nothing if the probable is already a failure, will do nothing if the predicate returns true, otherwise mutate the probable into a Probable.Failure
                      .map(entity -> entity.getName()); //change the value of the probable if the probable has a value. This in term will yield another probable.
  }
}
```

Here we call some repository and transform the probable into a String, but first we test if the entity is in fact valid.
If the probable did not have any value the original probable will be returned without content.

### map vs flatMap

For those who don't know when to use which, map is the default method used to map the direct value of the Probable in case you don't have
any nested
probables. But if you do have nested probables, use the flatMap method. This avoids having to deal with probables like
`Probable<Probable<String>>` and instead transforms it into a `Probable<String>`.

### Debugging

Since it can be confusing to work with a Probable.Failure that is the result of several `map()` or `flatMap()` calls, an
automatic debug message features is added (provided that you use the `slf4j-api`). Simply set your logging level to
DEBUG and you'll automatically see the result of every operation that happened inside your Probable.

### Probable vs Optional

Even though they might bare a lot of resemblance, Optionals are a different data structure. They provide the same basic
wrapping mechanism around a value as Probables, mapping functions included, but an Optional does not provide insight in
what went wrong and where. It has no context. The result of an Optional can only be null or non-null. Also, an Optional
does not uphold all the monad rules, while a Probable does. A Probable is
more flexible, since having no value does not mean something went wrong. This allows you to use Probables for void
processes as well. Other than that, a Probable provides a few more handy methods and functions which allow you to use
them much more broadly. When used correctly, the Probable API can be used to connect all of your business logic.
