# Probably

Simple monad library to encapsulate and propagate processing results.

Often when something deep in our code goes wrong, we have only our exceptions to rely on propagating
error messages. But what if what happens isn't an actual "exception"? Exceptions should be just that. Exceptional. For
everything else a simple Probable will suffice. Using Probables enables you to better avoid using exceptions as a
control flow mechanism (which is an anti-pattern).

A Probable can have one of three possible Types:

VALUE - which means that the process it encapsulated was successful and yielded a value
NOTHING - which means that the process it encapsulated was successful but did not yield a value (this must've been intentional)
FAILURE - which means that the process encountered an error or some validation upon the value did not pass

A Probable FAILURE will have a (custom) message inside it to provide more information about why it was a FAILURE.

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
passing the process as a function into the probableOf methods.

```java

import com.compilit.probably.Probable;

class Example {

  Probable<?> exampleMethod1() {
    if (everythingWentWellInAVoidProcess()) {
      return Probable.nothing();
    } else {
      return Probable.failure(TEST_MESSAGE);
    }
  }

  Probable<?> exampleMethod2() {
    if (everythingWentWellInAProcess()) {
      return Probable.value(content);
    } else {
      return Probable.failure(TEST_MESSAGE);
    }
  }

  Probable<?> exampleMethod3() {
    if (something.doesNotMeetOurExpectations()) {
      return Probable.failure("Reason");
    } else {
      return Probable.nothing();
    }
  }

  Probable<?> exampleMethod4() {
    return Probable.of(() -> doSomethingDangerous());
  }
  
}

```

### Chaining probables

The map and flatMap methods enable you to take your probable and apply a function to it. But only in case
it has a value. This means you can chain probable methods through a fluent API. Map and flatMap won't do anything to a null value. Otherwise, your original, possibly failed
Probable would be lost by the Probable encountering exceptions during the map and flatMap methods. Here is an example:

```java
class ExampleClass {
  //(...)

  Probable<String> getProbable(Long id) {
    return respository.findById(id)
                      .test(entity -> entity.canBeAccessed()) // will do nothing if the probable is already a failure, will do nothing if the predicate returns true, otherwise mutate the probable into a FAILURE
                      .map(entity -> entity.getName()); //change the value of the probable if the probable has a value. This in term will yield another probable.
  }
}
```

Here we call some repository and transform the probable into a String, but first we test if the entity is in fact valid.
If the probable did not have any value the original probable will be returned without content.

### map vs flatMap

For those who don't know when to use which, map is the default method used to chain operations in case you don't have
any nested
probables. But if you do have nested probables, use the flatMap method. This avoids having to deal with probables like
Probable<Probable<String>> and instead transforms it into a Probable<String>.

### Probable vs Optional

Even though they might bare some resemblance, Optionals are a different data structure. They provide the same basic
wrapping mechanism around a value as Probable, mapping functions included, but an Optional does not provide insight in
what went wrong and where. It has no context. The result of an Optional can only be null or non-null. A Probable is
more flexible, since having no value does not mean something went wrong. This allows you to use Probables for void
processes as well. Other than that, a Probable provides a few more handy methods and functions which allow you to use
them much more broadly.