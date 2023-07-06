package com.compilit.probably;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

/**
 * This is your entrypoint for all Probable operations.
 *
 * @param <T> The type of the Probable you wish to return.
 */
public class Probable<T> {

  private final T value;
  private final Type probableType;
  private final String message;
  private final Exception exception;

  private Probable(Type probableType, T value, String message, Exception exception) {
    this.probableType = probableType;
    this.value = value;
    this.message = message;
    this.exception = exception;
  }

  /**
   * Every Probable can be either of Type VALUE (meaning it contains a value), NOTHING (meaning it does not contain a
   * value) or FAILURE (meaning something went wrong or did not pass a validation)
   *
   * @return the Probable.Type of the Probable.
   */
  public Type getType() {
    return probableType;
  }

  /**
   * All Probables will have a default message which corresponds to their Type. But FAILURE messages can have a custom
   * message.
   *
   * @return the message of the Probable.
   */
  public String getMessage() {
    return message;
  }

  /**
   * During processing of an encapsulated process, any encountered exception will be set here. This value will only be
   * present if the Type is FAILURE.
   *
   * @return the exception that occurred in the Probable.
   */
  public Exception getException() {
    return exception;
  }

  public final boolean equals(Object obj) {
    if (obj == null) {
      return get() == null;
    }
    if (obj instanceof Probable<?>) {
      return Objects.equals(get(), ((Probable<?>) obj).get());
    }
    if (hasValue() && obj.getClass().isAssignableFrom(get().getClass())) {
      return Objects.equals(get(), obj);
    }
    return false;
  }

  public final int hashCode() {
    return Optional.ofNullable(get()).map(Object::hashCode).orElse(0);
  }

  public final String toString() {
    return String.valueOf(get());
  }


  /**
   * Find out of the given Probable has failed or not.
   *
   * @return true if the Probable.Type equals FAILURE.
   */
  public final boolean hasFailed() {
    return getType().equals(Type.FAILURE);
  }

  /**
   * Get the value of the Probable, which can in fact be null.
   *
   * @return the nullable value of the Probable.
   */
  public T get() {
    return orElse(null);
  }

  /**
   * Get the value of the Probable if present, otherwise return the public value.
   *
   * @param defaultValue the default value you wish to return in case this Probable does not have any.
   * @return the value of the Probable or the defaultValue.
   */
  public T orElse(T defaultValue) {
    if (value != null) {
      return value;
    }
    return defaultValue;
  }

  /**
   * Get the Probable if it contains a value, otherwise return the other Probable. A good, simple use case for this function would
   * be to find out if a resource already exists, and in case it doesn't, try to create a new one. Both paths would
   * return said Probable Resource. Note that it is possible to return an empty probable from this method.
   *
   * @param otherProbable the alternative Probable you wish to return in case this Probable is NOTHING or FAILURE.
   * @return the Probable or the other Probable.
   */
  public final Probable<T> or(Probable<T> otherProbable) {
    if (hasValue()) {
      return this;
    }
    return otherProbable;
  }

  /**
   * You never know if a Probable has any value until you check it or its Type.
   *
   * @return true if the Probable has value.
   */
  public final boolean hasValue() {
    return get() != null;
  }

  /**
   * You never know if a Probable has any value until you check it or its Type.
   *
   * @return true if the Probable has no value.
   */
  public final boolean isEmpty() {
    return !hasValue();
  }

  /**
   * In case of a successful Probable, apply the mapping function to the content (if present) to return a different
   * content type. It should be noted that the Probable status "SUCCESS" does not automatically mean the Probable has
   * value. Which is why the Probable of the thenApply function can be influenced by the mapping function that is
   * passed.
   *
   * @param mappingFunction, the operation you wish to apply to the value
   * @param <R>,             the return type
   * @return the final Probable
   */
  public final <R> Probable<R> map(Function<? super T, ? extends R> mappingFunction) {
    if (hasValue()) {
      var functionProbable = mappingFunction.apply(get());
      return of(() -> functionProbable);
    }
    if (hasFailed()) {
      return failure(getException(), getMessage());
    }
    return nothing();
  }

  /**
   * In case of a successful Probable, apply the mapping function to the content (if present) to return a different
   * content type. It should be noted that the Probable status "SUCCESS" does not automatically mean the Probable has
   * value. Which is why the Probable of the thenApply function can be influenced by the mapping function that is
   * passed.
   *
   * @param mappingFunction, the operation you wish to apply to the value
   * @param <R>,             the return type
   * @return the final Probable
   */
  public final <R> Probable<R> flatMap(Function<? super T, ? extends Probable<? extends R>> mappingFunction) {
    if (hasValue()) {
      return (Probable<R>) mappingFunction.apply(get());
    }
    if (hasFailed()) {
      return failure(getException(), getMessage());
    }
    return nothing();
  }

  /**
   * Validate the value of the Probable. The original Probable.Type is ignored unless the value complies with the given
   * predicate.
   *
   * @param predicate the validation you wish to perform on the value
   * @return the Probable if it complies with the given predicate, otherwise the Probable with the altered Probable.Type
   */
  public final Probable<T> test(Predicate<T> predicate) {
    return test(predicate, Messages.failedPredicate(predicate));
  }

  /**
   * Validate the value of the Probable. The original Probable.Type is ignored unless the value complies with the given
   * predicate.
   *
   * @param predicate      the validation you wish to perform on the value
   * @param failureMessage the message you wish to pass in case the predicate resolves to false
   * @return the Probable if it complies with the given predicate, otherwise the Probable with the altered Probable.Type
   */
  public final Probable<T> test(Predicate<T> predicate, String failureMessage) {
    try {
      boolean isValid = predicate.test(get());
      if (isValid) {
        return this;
      }
      return failure(failureMessage);
    } catch (Exception e) {
      return failure(e.getMessage());
    }
  }

  /**
   * Log the Probable on level INFO
   *
   * @return the original Probable, or the altered Probable in case of an exception
   */
  public final Probable<T> log() {
    return log(Level.INFO);
  }

  /**
   * Log the message and Probable on level INFO
   *
   * @param message the message you wish to log before the Probable log
   * @param args the structured arguments you wish to add to your log before the Probable log
   * @return the original Probable, or the altered Probable in case of an exception
   */
  public final Probable<T> log(String message, Object... args) {
    return log(Level.INFO, message, args);
  }

  /**
   * Log the Probable on the provided level
   *
   * @param level the level you wish the log the message on
   * @return the original Probable, or the altered Probable in case of an exception
   */
  public final Probable<T> log(Level level) {
    return log(level, "Probable processed");
  }

  /**
   * Log the Probable and message on the provided level
   *
   * @param level   the level you wish the log the message on
   * @param message the message you wish to log before the Probable log
   * @param args the structured arguments you wish to add to your log before the Probable log
   * @return the original Probable, or the altered Probable in case of an exception
   */
  public final Probable<T> log(Level level, String message, Object... args) {
    String probableMessage = getLogMessage(message);
    var encounteredException = getException();
    if (encounteredException != null) {
      LoggerFactory.getLogger(getClass()).atLevel(level).log(probableMessage, encounteredException);
    } else {
      LoggerFactory.getLogger(getClass()).atLevel(level).log(probableMessage, args);
    }
    return this;
  }

  /**
   * A generic empty Probable for a process or validation.
   *
   * @param <T> the type of the value.
   * @return a success Probable.
   */
  public static <T> Probable<T> nothing() {
    return new Probable<>(Type.NOTHING, null, Type.NOTHING.getDefaultMessage(), null);
  }

  /**
   * A generic success Probable for a process or validation.
   *
   * @param value the value of the Probable.
   * @param <T>   the type of the value.
   * @return a success Probable with value. Or an empty resource Probable if the content is null.
   */
  public static <T> Probable<T> value(T value) {
    return new Probable<>(Type.VALUE, value, Type.VALUE.getDefaultMessage(), null);
  }

  /**
   * A generic Probable for any encountered exceptions.
   *
   * @param <T>             the content type.
   * @param message         the error message.
   * @param formatArguments the message arguments you with to replace the '%s' (for example) symbol with.
   * @return an error occurred Probable with a message.
   */
  public static <T> Probable<T> failure(String message, String... formatArguments) {
    var actualMessage = MessageFormatter.formatMessage(message, (Object[]) formatArguments);
    return new Probable<>(Type.FAILURE, null, actualMessage, null);
  }

  /**
   * A generic Probable for any encountered exceptions.
   *
   * @param <T>             the content type.
   * @param exception       the exception that was encountered.
   * @param message         the error message.
   * @param formatArguments the message arguments you with to replace the '%s' (for example) symbol with.
   * @return an error occurred Probable with a message.
   */
  public static <T> Probable<T> failure(Exception exception, String message, String... formatArguments) {
    var actualMessage = MessageFormatter.formatMessage(message, (Object[]) formatArguments);
    return new Probable<>(Type.FAILURE, null, actualMessage, exception);
  }

  /**
   * A generic Probable that encapsulates a value. Returns a Success Probable with the supplied content if the value is
   * not null. This bind function is comparable to Optional.ofNullable(value).
   *
   * @param value the value that needs to be present in order for the Probable to be successful
   * @param <T>   the type of the value.
   * @return SuccessProbable or UnsuccessfulProbable with the exception message.
   */
  public static <T> Probable<T> of(T value) {
    if (value == null) {
      return nothing();
    }
    return value(value);
  }

  /**
   * A generic Probable that encapsulates a UnaryOperator process. Returns a Success Probable with the supplied content
   * if the UnaryOperator does not throw an Exception. If the Supplier throws an Exception, it returns a corresponding
   * Probable with the exception message added to the Probable.
   *
   * @param probableUnaryOperator the function that returns the final Probable.
   * @param <T>                   the type of the value.
   * @return SuccessProbable or UnsuccessfulProbable with the exception message.
   */
  public static <T> Probable<T> of(UnaryOperator<Probable<T>> probableUnaryOperator) {
    var defaultProbable = Probable.<T>nothing();
    try {
      return probableUnaryOperator.apply(defaultProbable);
    } catch (Exception exception) {
      return failure(exception, exception.getMessage());
    }
  }

  /**
   * A generic Probable that encapsulates a supplying process. Returns a Success Probable with the supplied content if
   * the Supplier does not throw an Exception. If the Supplier throws an Exception, it returns a corresponding Probable
   * with the exception message added to the Probable.
   *
   * @param supplier the content-supplying function.
   * @param <T>      the type of the value.
   * @return SuccessProbable or UnsuccessfulProbable with the exception message.
   */
  public static <T> Probable<T> of(Supplier<T> supplier) {
    try {
      return of(supplier.get());
    } catch (Exception exception) {
      return failure(exception, exception.getMessage());
    }
  }

  /**
   * A generic Probable that encapsulates a predicate. Returns a Success Probable with the value if the predicate
   * resolves to true. And an Unprocessable Probable if it resolves to false. If the Predicate throws an Exception, it
   * returns an UnsuccessfulProbable with the exception message added to the Probable.
   *
   * @param predicate the predicate which to apply to the value.
   * @param value     the value which needs to be tested by the predicate.
   * @param <T>       the type of the value.
   * @return SuccessProbable, UnprocessableProbable or UnsuccessfulProbable with the exception message.
   */
  public static <T> Probable<T> of(Predicate<T> predicate, T value) {
    try {
      return of(value).test(predicate);
    } catch (Exception exception) {
      return failure(exception, exception.getMessage());
    }
  }

  private String getLogMessage(String message) {
    return String.format(
      Messages.BASE_LOG_MESSAGE,
      message,
      getType(),
      get(),
      getMessage()
    );
  }

  /**
   * A status enum for all probables.
   */
  public enum Type {
    /**
     * Status for all probables containing a value.
     */
    VALUE(Messages.VALUE),

    /**
     * Status for all empty probables that did not encounter any issues.
     */
    NOTHING(Messages.NOTHING),

    /**
     * Status for all failed probables.
     */
    FAILURE(Messages.FAILURE);

    private final String defaultMessage;

    Type(String defaultMessage) {this.defaultMessage = defaultMessage;}


    private String getDefaultMessage() {
      return defaultMessage;
    }
  }

}
