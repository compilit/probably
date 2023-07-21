package com.compilit.probably;

import static com.compilit.probably.Messages.ACCEPT_FAILED;
import static com.compilit.probably.Messages.ACCEPT_SUCCESSFUL;
import static com.compilit.probably.Messages.FLATMAP_APPLIED;
import static com.compilit.probably.Messages.FLATMAP_NOT_APPLIED;
import static com.compilit.probably.Messages.MAP_APPLIED;
import static com.compilit.probably.Messages.MAP_NOT_APPLIED;
import static com.compilit.probably.Messages.RUN_CALLED;
import static com.compilit.probably.Messages.RUN_NOT_CALLED;
import static com.compilit.probably.Messages.TEST_CALL_FAILED;
import static com.compilit.probably.Messages.TRANSFORMED_INTO_EMPTY_STREAM;
import static com.compilit.probably.Messages.TRANSFORMED_INTO_STREAM;
import static com.compilit.probably.Messages.exceptionWasThrown;
import static com.compilit.probably.Messages.messageRequired;
import static com.compilit.probably.Messages.paramRequired;
import static com.compilit.probably.Messages.testCallSuccessful;
import static com.compilit.probably.ProbableLogger.logDebugEvent;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

/**
 * A {@code Probable} encapsulates more than just a value, as opposed to an {@code Optional}. In case some process
 * during the retrieval of said value failed, or some predicate about the value returned false, you now have a context
 * to find out what went wrong and why. There are three possible outcomes to a process: {@code Probable.Value},
 * {@code Probable.Nothing} and {@code Probable.Failure}. {@code Probable.Value}s encapsulate a value from a "happy"
 * flow. {@code Probable.Nothing}s encapsulate nothing, from a "happy" flow (a void process basically), and a
 * {@code Probable.Failure} encapsulates a failure message and possibly an Exception. All will have a default message,
 * but also provide the possibility to add custom messages and even Exceptions to its context.
 * <p>
 * Contrary to Optionals, Probables are partially extendable. So if you think you need more types than the three
 * currently implemented, you can add them. To prevent introducing unwanted behavior, most Probable methods are made
 * final. So you can extend, but not alter.
 * </p>
 *
 * @param <T> The type of the Probable you wish to return.
 */
public abstract class Probable<T> {

  private final T value;
  private final String message;
  private final Exception exception;
  static final Logger LOGGER = LoggerFactory.getLogger(Probable.class);

  /**
   * The main constructor for all Probable subtypes. All Probable subtypes should fulfil this simple contract: a
   * Probable should have a message which conveys its context/meaning.
   *
   * @param value           the nullable value of the probable
   * @param exception       the nullable exception encountered during the processing of the probable
   * @param message         the non-{@code null} message for the probable
   * @param formatArguments the optional format argument for the message,
   * @throws NullPointerException if message is {@code null}
   */
  protected Probable(T value,
                     Exception exception,
                     String message,
                     Object... formatArguments) {
    this.value = value;
    this.message = MessageFormatter.formatMessage(
      Objects.requireNonNull(message, messageRequired()),
      formatArguments
    );
    this.exception = exception;
  }

  /**
   * Returns the nullable value.
   *
   * @return the nullable value described by this {@code Probable}
   */
  public final T get() {
    return value;
  }

  /**
   * All Probables will have a default message which corresponds to their subtype. But Probable.Failure subtypes can
   * have a custom message.
   *
   * @return the message of the Probable.
   */
  public final String getMessage() {
    return message;
  }

  /**
   * During processing of an encapsulated process, any encountered exception will be set here. This value will only be
   * present if the Probable is a Probable.Failure.
   *
   * @return the exception that occurred in the Probable.
   */
  public final Exception getException() {
    return exception;
  }

  /**
   * Find out of the given Probable has failed or not.
   *
   * @return true if the Probable is an instance of Probable.Failure.
   */
  public final boolean hasFailed() {
    return this instanceof Probable.Failure;
  }

  /**
   * Get the value of the Probable if present, otherwise return the public value.
   *
   * @param other the default value you wish to return in case this Probable does not have any.
   * @return the value of the Probable or the other.
   */
  public final T orElse(T other) {
    if (value != null) {
      return value;
    }
    return other;
  }

  /**
   * If a value is present, returns the value, otherwise returns the result produced by the supplying function.
   *
   * @param supplier the supplying function that produces a value to be returned
   * @return the value, if present, otherwise the result produced by the supplying function
   * @throws NullPointerException if no value is present and the supplying function is {@code null}
   */
  public final T orElse(Supplier<? extends T> supplier) {
    Objects.requireNonNull(supplier, paramRequired("supplier"));
    return value != null ? value : supplier.get();
  }

  /**
   * Get the Probable if it contains a value, otherwise return the other Probable. A good, simple use case for this
   * function would be to find out if a resource already exists, and in case it doesn't, try to create a new one. Both
   * paths would return said Probable Resource. Note that it is possible to return an empty probable from this method.
   *
   * @param otherProbable the alternative Probable you wish to return in case this Probable is Probable.Nothing or
   *                      Probable.Failure.
   * @return the Probable or the other Probable.
   * @throws NullPointerException if the otherProbable is {@code null}
   */
  public final Probable<T> or(Probable<T> otherProbable) {
    Objects.requireNonNull(otherProbable, paramRequired("otherProbable"));
    if (hasValue()) {
      return this;
    }
    return otherProbable;
  }

  /**
   * Get the Probable if it contains a value, otherwise return the other Probable. A good, simple use case for this
   * function would be to find out if a resource already exists, and in case it doesn't, try to create a new one. Both
   * paths would return said Probable Resource. Note that it is possible to return an empty probable from this method.
   *
   * @param probableSupplier the alternative Probable you wish to return through a Supplier in case this Probable is
   *                         Probable.Nothing or Probable.Failure.
   * @return the Probable or the other Probable.
   * @throws NullPointerException if the probableSupplier is {@code null}
   */
  public final Probable<T> or(Supplier<? extends Probable<? extends T>> probableSupplier) {
    Objects.requireNonNull(probableSupplier, paramRequired("probableSupplier"));
    if (hasValue()) {
      return this;
    }
    return (Probable<T>) probableSupplier.get();
  }

  /**
   * You never know if a Probable has any value until you check it or its subtype instance.
   *
   * @return {@code true} if a value is present, otherwise {@code false}
   */
  public final boolean hasValue() {
    return value != null;
  }

  /**
   * You never know if a Probable has any value until you check it or its subtype instance.
   *
   * @return true if the Probable has no value.
   */
  public final boolean isEmpty() {
    return !hasValue();
  }

  /**
   * In case of a Probable.Value, apply the mapping function to the value to return a different value type.
   *
   * @param mappingFunction, the operation you wish to apply to the value
   * @param <R>,             the return type
   * @return the final Probable
   * @throws NullPointerException if the mappingFunction is {@code null}
   */
  public final <R> Probable<R> map(Function<? super T, ? extends R> mappingFunction) {
    Objects.requireNonNull(mappingFunction, paramRequired("mappingFunction"));
    if (hasValue()) {
      return failureOnException(probable -> {
        var newValue = mappingFunction.apply(value);
        var newProbable = of(newValue);
        logDebugEvent(newProbable, MAP_APPLIED);
        return newProbable;
      }, MAP_NOT_APPLIED);
    }
    return failureOrNothing(MAP_NOT_APPLIED);
  }

  /**
   * In case of a nested Probable.Value, apply the mapping function to the value to return a different value type.
   *
   * @param mappingFunction, the operation you wish to apply to the value
   * @param <R>,             the return type
   * @return the final Probable
   * @throws NullPointerException if the mappingFunction is {@code null}
   */
  public final <R> Probable<R> flatMap(Function<? super T, ? extends Probable<? extends R>> mappingFunction) {
    Objects.requireNonNull(mappingFunction, paramRequired("mappingFunction"));
    Probable<T> baseProbable = new InternalProbable<>(this).getDeepestNestedProbable();
    if (hasValue()) {
      return failureOnException(probable -> {
        var newProbable = mappingFunction.apply(baseProbable.value);
        logDebugEvent(newProbable, FLATMAP_APPLIED);
        return (Probable<R>) newProbable;
      }, FLATMAP_NOT_APPLIED);
    }
    return failureOrNothing(FLATMAP_NOT_APPLIED);
  }

  /**
   * Validate the value of the Probable. But only if the Probable is an instance of Probable.Value
   *
   * @param predicate the validation you wish to perform on the value
   * @return the same Probable if it complies with the given predicate, otherwise the Probable will be changed into a
   * Probable.Failure
   * @throws NullPointerException if the predicate is {@code null}
   */
  public final Probable<T> test(Predicate<T> predicate) {
    Objects.requireNonNull(predicate, paramRequired("predicate"));
    return test(predicate, Messages.failedPredicate(this));
  }

  /**
   * Validate the value of the Probable. But only if the Probable is an instance of Probable.Value. Note that, if you
   * are dealing with nested Probables, this test will be applied on the first nested Probable inside the original. When
   * this behavior is not desired, don't forget you can always use the flatMap method to flatten the nested Probable.
   *
   * @param predicate      the validation you wish to perform on the value
   * @param failureMessage the message you wish to pass in case the predicate resolves to false
   * @return the same Probable if it complies with the given predicate, otherwise the Probable will be changed into a
   * Probable.Failure
   * @throws NullPointerException if the predicate, or te failureMessage is {@code null}
   */
  public final Probable<T> test(Predicate<T> predicate, String failureMessage) {
    Objects.requireNonNull(predicate, paramRequired("predicate"));
    Objects.requireNonNull(failureMessage, paramRequired("failureMessage"));
    return failureOnException(probable -> {
      boolean isValid = predicate.test(value);
      logDebugEvent(this, testCallSuccessful(isValid));
      if (isValid) {
        return this;
      }
      return failure(failureMessage);
    }, TEST_CALL_FAILED);
  }

  private <R> Probable<R> failureOnException(Function<Probable<T>, Probable<R>> unaryOperator,
                                             String exceptionMessage) {
    try {
      return unaryOperator.apply(this);
    } catch (Exception e) {
      logDebugEvent(this, exceptionMessage);
      return failure(e, exceptionWasThrown(e));
    }
  }

  /**
   * If the Probable has a value, performs the given consumer with the value, otherwise does nothing.
   *
   * @param consumer the consumer to be performed, if a value is present
   * @return this Probable instance or a Probable.Failure if the consumer throws an Exception.
   * @throws NullPointerException if the given consumer is {@code null}
   */
  public final <V> Probable<T> thenAccept(Consumer<? super V> consumer) {
    Objects.requireNonNull(consumer, paramRequired("consumer"));
    if (hasValue()) {
      return failureOnException(probable -> {
        logDebugEvent(this, ACCEPT_SUCCESSFUL);
        consumer.accept((V) value);
        return this;
      }, ACCEPT_FAILED);
    }
    return this;
  }

  /**
   * If the Probable hasn't failed, runs the given runnable, otherwise does nothing.
   *
   * @param runnable the runnable to be performed in case this Probable hasn't failed.
   * @return this Probable instance or a Probable.Failure if the runnable throws an Exception.
   * @throws NullPointerException if the given consumer is {@code null}
   */
  public final Probable<T> thenRun(Runnable runnable) {
    Objects.requireNonNull(runnable, paramRequired("runnable"));
    if (!hasFailed()) {
      return failureOnException(probable -> {
        runnable.run();
        logDebugEvent(probable, RUN_CALLED);
        return this;
      }, RUN_NOT_CALLED);
    }
    return this;
  }

  /**
   * If a value is present, returns a sequential {@link Stream} containing only that value, otherwise returns an empty
   * {@code Stream}.
   *
   * @return the value as a {@code Stream}
   * @apiNote This method can be used to transform a {@code Stream} of Probable elements to a {@code Stream} of present
   * value elements:
   * <pre>{@code
   *     Stream<Probable<T>> os = ..
   *     Stream<T> s = os.flatMap(Probable::stream)
   * }</pre>
   */
  public Stream<T> stream() {
    if (!hasValue()) {
      logDebugEvent(this, TRANSFORMED_INTO_EMPTY_STREAM);
      return Stream.empty();
    } else {
      logDebugEvent(this, TRANSFORMED_INTO_STREAM);
      return Stream.of(value);
    }
  }

  /**
   * Log the Probable with level INFO
   *
   * @return the original Probable
   */
  public final Probable<T> log() {
    return log(null);
  }

  /**
   * Log the message and Probable with level INFO
   *
   * @param message the message you wish to log before the Probable log
   * @param args    the structured arguments you wish to add to your log before the Probable log
   * @return the original Probable
   */
  public final Probable<T> log(String message, Object... args) {
    return log(Level.INFO, message, args);
  }

  /**
   * Log the Probable and message on the provided level
   *
   * @param level   the level you wish the log the message on
   * @param message the message you wish to log before the Probable log
   * @param args    the structured arguments you wish to add to your log before the Probable log
   * @return the original Probable
   */
  public final Probable<T> log(Level level, String message, Object... args) {
    Objects.requireNonNull(level, paramRequired("level"));
    ProbableLogger.log(this, level, message, args);
    return this;
  }

  /**
   * Log the Probable and message on level ERROR if the Probable has failed.
   *
   * @param message the message you wish to log before the Probable log
   * @param args    the structured arguments you wish to add to your log before the Probable log
   * @return the original Probable
   */
  public final Probable<T> logFailure(String message, Object... args) {
    if (hasFailed()) {
      return log(Level.ERROR, message, args);
    }
    return this;
  }

  /**
   * If a value is present, returns the value, otherwise returns the result produced by the supplying function.
   *
   * @param supplier the supplying function that produces a value to be returned
   * @return the value, if present, otherwise the result produced by the supplying function
   * @throws NullPointerException if no value is present and the supplying function is {@code null}
   */
  public T orElseGet(Supplier<? extends T> supplier) {
    return value != null ? value : supplier.get();
  }

  /**
   * If a value is present, returns the value, otherwise throws {@code NoSuchElementException}.
   *
   * @return the non-{@code null} value described by this {@code Probable}
   * @throws NoSuchElementException if no value is present
   * @since 10
   */
  public T orElseThrow() {
    return orElseThrow(() -> new NoSuchElementException("No value present"));
  }

  /**
   * If a value is present, returns the value, otherwise throws an exception produced by the exception supplying
   * function.
   *
   * @param <X>               Type of the exception to be thrown
   * @param exceptionSupplier the supplying function that produces an exception to be thrown
   * @return the value, if present
   * @throws X                    if no value is present
   * @throws NullPointerException if no value is present and the exception supplying function is {@code null}
   * @apiNote A method reference to the exception constructor with an empty argument list can be used as the supplier.
   * For example, {@code IllegalStateException::new}
   */
  public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
    if (value != null) {
      return value;
    } else {
      throw exceptionSupplier.get();
    }
  }

  /**
   * If the given argument is null, return true if this value of this Probable is also null. If the given argument is an
   * instance of Probable, compare the value of both. If this probable has a value return true if the given argument
   * class is assignable from the class of the value inside this Probable. All other cases are considered false.
   *
   * @param obj the other value you wish to test against the nullable value inside this Probable
   * @return true if the value inside this Probable is considered equal to the given argument.
   */
  @Override
  public final boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof Probable<?>) {
      return Objects.equals(value, ((Probable<?>) obj).get());
    }
    return Objects.equals(obj, value);
  }

  /**
   * @return the hashcode value of the wrapped value, otherwise return 0.
   */
  @Override
  public final int hashCode() {
    return Objects.hashCode(value);
  }

  /**
   * @return the String value of the wrapped value.
   */
  @Override
  public final String toString() {
    return String.valueOf(value);
  }

  /**
   * A generic Probable that encapsulates a nullable value. Returns a Probable.Value with the supplied content if the
   * value is not null. Otherwise, it returns a Probable.Nothing. This bind function is comparable to
   * Optional.ofNullable(value).
   *
   * @param value the nullable value you wish to wrap in this Probable.
   * @param <T>   the type of the value.
   * @return Probable.Value or Probable.Nothing.
   */
  public static <T> Probable<T> of(T value) {
    if (value == null) {
      return nothing();
    }
    return new Probable.Value<>(value);
  }

  /**
   * A generic Probable that encapsulates a supplying process. Returns a Probable.Value with the supplied content if the
   * Supplier does not throw an Exception. If the Supplier throws an Exception, it returns a corresponding Probable with
   * the exception message added to the Probable.
   *
   * @param supplier the content-supplying function.
   * @param <T>      the type of the value.
   * @return Probable.Value, Probable.Nothing, or Probable.Failure with the exception message.
   */
  public static <T> Probable<T> of(Supplier<T> supplier) {
    try {
      if (supplier == null) {
        return nothing();
      }
      return of(supplier.get());
    } catch (Exception exception) {
      return failure(exception, exception.getMessage());
    }
  }

  /**
   * A generic value-containing Probable. Note: it is advised to always make use of the `of(T value)` or `of(Supplier<T>
   * supplier)` functions to create new Probable instances
   *
   * @param <T> the type of the value you would wish to return from this Probable context. This way you'll be able to
   *            chain methods like the {@code orElse(T value)} method. If you aren't going to chain methods like that,
   *            just use {@code Probable<Void>}
   * @return a Probable.Value.
   */
  public static <T> Probable<T> value(T value) {
    return new Probable.Value<>(Objects.requireNonNull(value));
  }

  /**
   * A generic value-containing Probable. Note: it is advised to always make use of the `of(T value)` or `of(Supplier<T>
   * supplier)` functions to create new Probable instances
   *
   * @param <T> the type of the value you would wish to return from this Probable context. This way you'll be able to
   *            chain methods like the {@code orElse(T value)} method. If you aren't going to chain methods like that,
   *            just use {@code Probable<Void>}
   * @return a Probable.Value.
   */
  public static <T> Probable<T> value(T value, String message, Object... formatArguments) {
    return new Probable.Value<>(Objects.requireNonNull(value), message, formatArguments);
  }

  /**
   * A generic empty Probable for a process or validation. Note: it is advised to always make use of the `of(T value)`
   * or `of(Supplier<T> supplier)` functions to create new Probable instances
   *
   * @param <T> the type of the value you would wish to return from this Probable context. This way you'll be able to
   *            chain methods like the {@code orElse(T value)} method. If you aren't going to chain methods like that,
   *            just use {@code Probable<Void>}
   * @return a Probable.Nothing.
   */
  public static <T> Probable<T> nothing() {
    return new Probable.Nothing<>();
  }

  /**
   * A generic empty Probable for a process or validation. Note: it is advised to always make use of the `of(T value)`
   * or `of(Supplier<T> supplier)` functions to create new Probable instances
   *
   * @param <T>             the type of the value you would wish to return from this Probable context. This way you'll
   *                        be able to chain methods like the {@code orElse(T value)} method. If you aren't going to
   *                        chain methods like that, just use {@code Probable<Void>}
   * @param message         the error message.
   * @param formatArguments the message arguments you with to replace the '%s' (for example) symbol with.
   * @return a Probable.Nothing.
   */
  public static <T> Probable<T> nothing(String message, Object... formatArguments) {
    return new Probable.Nothing<>(message, formatArguments);
  }

  /**
   * A generic Probable for any encountered exceptions. Note: it is advised to always make use of the `of(T value)` or
   * `of(Supplier<T> supplier)` functions to create new Probable instances
   *
   * @param <T>             the content type.
   * @param message         the error message.
   * @param formatArguments the message arguments you with to replace the '%s' (for example) symbol with.
   * @return an error occurred Probable with a message.
   */
  public static <T> Probable<T> failure(String message, Object... formatArguments) {
    Objects.requireNonNull(message, messageRequired());
    return new Probable.Failure<>(message, formatArguments);
  }

  /**
   * A generic Probable for any encountered exceptions. Note: it is advised to always make use of the `of(T value)` or
   * `of(Supplier<T> supplier)` functions to create new Probable instances
   *
   * @param <T>             the content type.
   * @param exception       the exception that was encountered.
   * @param message         the error message.
   * @param formatArguments the message arguments you with to replace the '%s' (for example) symbol with.
   * @return an error occurred Probable with a message.
   */
  public static <T> Probable<T> failure(Exception exception,
                                        String message,
                                        Object... formatArguments) {
    Objects.requireNonNull(message, messageRequired());
    return new Probable.Failure<>(exception, message, formatArguments);
  }

  /**
   * A Probable.Value contains a value and hasn't encountered any exceptions or predicate failures.
   *
   * @param <T> the return type of the value inside the Probable.
   */
  public static class Value<T> extends Probable<T> {

    private Value(T value) {
      this(value, Messages.NOTHING_TO_REPORT);
    }

    private Value(T value, String message, Object... formatArguments) {
      super(value, null, message, formatArguments);
    }
  }

  /**
   * A Probable.Nothing has no value and hasn't encountered any exceptions or predicate failures.
   *
   * @param <T> the type of the value you would wish to return from this Probable context. This way you'll be able to
   *            chain methods like the {@code orElse(T value)} method. If you aren't going to chain methods like that,
   *            just use {@code Probable<Void>}
   */
  public static class Nothing<T> extends Probable<T> {

    private Nothing() {
      this(Messages.NOTHING_TO_REPORT);
    }

    private Nothing(String message, Object... formatArguments) {
      super(null, null, message, formatArguments);
    }
  }

  /**
   * A Probable.Failure has no value because it encountered an exception or predicate failure about the value.
   *
   * @param <T> the return type of the value inside the Probable.
   */
  public static class Failure<T> extends Probable<T> {

    private Failure(String message, Object... formatArguments) {
      this(null, message, formatArguments);
    }

    private Failure(Exception exception, String message, Object... formatArguments) {
      super(null, exception, message, formatArguments);
    }
  }

  private <R> Probable<R> failureOrNothing(String eventMessage) {
    logDebugEvent(this, eventMessage);
    if (hasFailed()) {
      return failure(getException(), getMessage());
    }
    return nothing();
  }

}
