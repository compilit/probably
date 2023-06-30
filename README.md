# Probably

Simple monad library to encapsulate and propagate processing probables.

Often when something deep in our code goes wrong, we have only our exceptions to rely on propagating
error messages. But what if what happens isn't an actual "exception"? Exceptions should be just that. Exceptional. For
everything else a simple Probable will suffice. Using probables also enables you to better avoid using exceptions as a
control flow mechanism (which is an anti-pattern).

### installation

Get this dependency with the latest version

```xml

<dependency>
  <artifactId>probably</artifactId>
  <groupId>com.compilit</groupId>
</dependency>
```

### usage

Everything can be handled through the Probable interface. Whenever you have some process that could
possibly fail, make sure that it returns a Probable. Which Probable should be returned can be chosen manually or by
passing
the process as a function into the probableOf methods.

```java

import com.compilit.probably.Probable;

class Example {

  Probable<?> exampleMethod1() {
    if (everythingWentWellInAVoidProcess()) {
      return Probable.successful();
    } else {
      return Probable.unsuccessful(TEST_MESSAGE);
    }
  }

  Probable<?> exampleMethod2() {
    if (everythingWentWellInAProcess()) {
      return Probable.successful(content);
    } else {
      return Probable.unsuccessful(TEST_MESSAGE);
    }
  }

  Probable<?> exampleMethod3() {
    if (something.doesNotMeetOurExpectations()) {
      return Probable.unsuccessful("Reason");
    } else {
      return Probable.unsuccessful(TEST_MESSAGE);
    }
  }

  Probable<?> exampleMethod4() {
    return Probable.probableOf(() -> doSomethingDangerous());
  }

  Probable<?> exampleMethod5() {
    return Probable.<SomeOtherType>transform(Probable.<OneType>unsuccessful()); // Returns the unsuccessful probable, with the matching return type.
  }
}

```

### Chaining probables

The map and flatMap methods enable you to take your probable and apply a function to it. But only in case
it is successful. This means you can chain probable methods through a fluent API. Since probables can be successful but
not have any contents, map and flatMap won't do anything to a null content. Otherwise your original, possible failed
Probable would be lost by the Probable encountering exceptions during the map and flatMap methods. Here is an example:

```java
class ExampleClass {
  //(...)

  Probable<String> getProbable(Long id) {
    return respository.findById(id)
                      .test(entity -> entity.isValid()) // will do nothing if the probable is already unsuccessful, will do nothing if the predicate returns true, otherwise mutate the probable into Unprocessable
                      .map(entity -> entity.getName()); //change the contents of the probable if the probable is successful. This in term will yield another probable.
  }
}
```

Here we call some repository and transform the probable into a String, but first we test if the entity is in fact valid.
If the probable was not successful the original probable will be returned without content.

### map vs flatMap

For those who don't know when to use which, map is the default method used to chain operations in case you don't have
any nested
probables. But if you do have nested probables, use the flatMap method. This avoids having to deal with probables like
Probable<Probable<String>> and instead transforms it into a Probable<String>.

### Probable vs Optional

Even though they might bare some resemblance, Optionals are a different data structure. They provide the same basic
wrapping mechanism around a value as Probable, mapping functions included, but an Optional does not provide insight in
what went wrong and where. It has no context. The result of an Optional can only be null or non-null. A Probable is much
more flexible, since having no contents does not mean something went wrong. This allows you to use Probables for void
processes as well. Other than that, a Probable provides a few more handy methods and functions which allow you to use
them much more broadly.