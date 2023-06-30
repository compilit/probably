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

  private final T contents;
  private final Type probableType;
  private final String message;
  private final Exception exception;

  protected Probable(Type probableType) {
    this(probableType, probableType.getDefaultMessage());
  }

  protected Probable(Type probableType, String message) {
    this(probableType, null, null, message);
  }

  protected Probable(Type probableType, T contents) {
    this(probableType, contents, null, probableType.getDefaultMessage());
  }

  protected Probable(Type probableType, T contents, Exception exception, String message) {
    this.probableType = probableType;
    this.contents = contents;
    this.exception = exception;
    this.message = message;
  }

  /**
   * @return the ProbableType of the Probable.
   */
  public Type geType() {
    return probableType;
  }

  /**
   * @return the message of the Probable.
   */
  public String getMessage() {
    return message;
  }

  /**
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
      return Objects.equals(get(), ((Probable<?>)obj).get());
    }
    if (hasContents() && obj.getClass().isAssignableFrom(get().getClass())) {
      return Objects.equals(get(),  obj);
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
   * @return true if the ProbableType equals SUCCESS.
   */
  public final boolean isSuccessful() {
    return geType().equals(Type.SUCCESSFUL);
  }

  /**
   * Find out of the given Probable is in fact successful or not. A successful Probable does not imply that it has
   * contents.
   *
   * @return true if the ProbableType does not equal SUCCESS.
   */
  public final boolean isUnsuccessful() {
    return !isSuccessful();
  }

  /**
   * Get the contents of the Probable, which can in fact be null.
   *
   * @return the nullable contents of the Probable.
   */
  public T get() {
    return orElse(null);
  }

  /**
   * Get the contents of the Probable if present, otherwise return the public value.
   *
   * @return the contents of the Probable or the defaultValue.
   */
  public T orElse(T defaultValue) {
    if (isSuccessful()) {
      return contents;
    }
    return defaultValue;
  }

  /**
   * Get the Probable if present, otherwise return the other Probable.
   *
   * @return the alternative the Probable or the other Probable.
   */
  public final Probable<T> or(Probable<T> otherProbable) {
    if (isSuccessful()) {
      return this;
    }
    return otherProbable;
  }

  /**
   * @return true if the Probable has contents.
   */
  public final boolean hasContents() {
    return get() != null;
  }

  /**
   * @return true if the Probable has no contents.
   */
  public final boolean isEmpty() {
    return !hasContents();
  }

  /**
   * In case of a successful Probable, apply the mapping function to the content (if present) to return a different content type. It
   * should be noted that the Probable status "SUCCESS" does not automatically mean the Probable has contents. Which is why
   * the Probable of the thenApply function can be influenced by the mapping function that is passed.
   *
   * @param mappingFunction, the operation you wish to apply to the contents
   * @param <R>,             the return type
   * @return the final Probable
   */
  public final <R> Probable<R> map(Function<? super T, ? extends R> mappingFunction) {
    if (isSuccessful() && hasContents()) {
      var functionProbable = mappingFunction.apply(get());
      if (functionProbable instanceof Probable<?>) {
        return (Probable<R>) functionProbable;
      }
      return probableOf(() -> functionProbable);
    }
    return transform(this);
  }

  /**
   * Transform a Probable of T1 to a Probable of T2 Since the return type can be different at compile-time, but the same at
   * runtime, this method will allow you to continue your stream as planned.
   *
   * @param <R>, the return type
   * @return the final Probable
   */
  public final <R> Probable<R> transform() {
    return transform(this);
  }

  /**
   * In case of a successful Probable, apply the mapping function to the content (if present) to return a different content type. It
   * should be noted that the Probable status "SUCCESS" does not automatically mean the Probable has contents. Which is why
   * the Probable of the thenApply function can be influenced by the mapping function that is passed.
   *
   * @param mappingFunction, the operation you wish to apply to the contents
   * @param <R>,             the return type
   * @return the final Probable
   */
  public final <R> Probable<R> flatMap(Function<? super T, ? extends Probable<? extends R>> mappingFunction) {
    if (isSuccessful() && hasContents()) {
      return (Probable<R>) mappingFunction.apply(get());
    }
    return transform(this);
  }

  /**
   * Validate the contents of the Probable. The original ProbableType is ignored unless the contents complies with the given
   * predicate.
   *
   * @param predicate the validation you wish to perform on the contents
   * @return the Probable if it complies with the given predicate, otherwise the Probable with the altered ProbableType
   */
  public final Probable<T> test(Predicate<T> predicate) {
    boolean isValid = orDefault(() -> predicate.test(get()), false);
    if (isValid) {
      return this;
    }
    return probableOf(Type.FAILED, Messages.failedPredicate(predicate), get(), null);
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
   * @return the original Probable, or the altered Probable in case of an exception
   */
  public final Probable<T> log(String message) {
    return log(Level.INFO, message);
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
   * @return the original Probable, or the altered Probable in case of an exception
   */
  public final Probable<T> log(Level level, String message) {
    String probableMessage = getLogMessage(message);
    var encounteredException = getException();
    if (encounteredException != null) {
      LoggerFactory.getLogger(getClass()).atLevel(level).log(probableMessage, encounteredException);
    } else {
      LoggerFactory.getLogger(getClass()).atLevel(level).log(probableMessage);
    }
    return this;
  }

  /**
   * A generic success Probable for a process or validation.
   *
   * @param <T> the type of the contents.
   * @return a success Probable.
   */
  public static <T> Probable<T> successful() {
    return new Probable<>(Type.SUCCESSFUL);
  }

  /**
   * A generic success Probable for a process or validation.
   *
   * @param contents the contents of the Probable.
   * @param <T>      the type of the contents.
   * @return a success Probable with contents. Or an empty resource Probable if the content is null.
   */
  public static <T> Probable<T> successful(T contents) {
    return new Probable<>(Type.SUCCESSFUL, contents);
  }

  /**
   * A generic Probable for any encountered exceptions.
   *
   * @param <T>             the content type.
   * @param message         the error message.
   * @param formatArguments the message arguments you with to replace the '%s' (for example) symbol with.
   * @return an error occurred Probable with a message.
   */
  public static <T> Probable<T> failed(String message, String... formatArguments) {
    var actualMessage = MessageFormatter.formatMessage(message, (Object[]) formatArguments);
    return new Probable<>(Type.FAILED, actualMessage);
  }

  /**
   * A generic Probable for any encountered exceptions.
   *
   * @param <T>             the content type.
   * @param message         the error message.
   * @param formatArguments the message arguments you with to replace the '%s' (for example) symbol with.
   * @return an error occurred Probable with a message.
   */
  public static <T> Probable<T> failed(Exception exception, String message, String... formatArguments) {
    var actualMessage = MessageFormatter.formatMessage(message, (Object[]) formatArguments);
    return new Probable<>(Type.FAILED, null, exception, actualMessage);
  }

  /**
   * A generic Probable that encapsulates a supplying process. Returns a Success Probable with the supplied content if the
   * Supplier does not throw an Exception. If the Supplier throws an Exception, it returns a corresponding Probable with
   * the exception message added to the Probable.
   *
   * @param probableUnaryOperator the function that returns the final Probable.
   * @param <T>                 the type of the contents.
   * @return SuccessProbable or UnsuccessfulProbable with the exception message.
   */
  public static <T> Probable<T> probableOf(UnaryOperator<Probable<T>> probableUnaryOperator) {
    var defaultProbable = Probable.<T>successful();
    try {
      return probableUnaryOperator.apply(defaultProbable);
    } catch (Exception exception) {
      return failed(exception, exception.getMessage());
    }
  }

  /**
   * A generic Probable that encapsulates a supplying process. Returns a Success Probable with the supplied content if the
   * Supplier does not throw an Exception. If the Supplier throws an Exception, it returns a corresponding Probable with
   * the exception message added to the Probable.
   *
   * @param supplier the content-supplying function.
   * @param <T>      the type of the contents.
   * @return SuccessProbable or UnsuccessfulProbable with the exception message.
   */
  public static <T> Probable<T> probableOf(Supplier<T> supplier) {
    try {
      var tmp = supplier.get();
      if (tmp instanceof Probable<?>) {
        return ((Probable<?>) tmp).transform();
      }
      return successful(supplier.get());
    } catch (Exception exception) {
      return failed(exception, exception.getMessage());
    }
  }

  /**
   * A generic Probable that encapsulates a predicate. Returns a Success Probable with the value if the predicate resolves
   * to true. And an Unprocessable Probable if it resolves to false. If the Predicate throws an Exception, it returns an
   * UnsuccessfulProbable with the exception message added to the Probable.
   *
   * @param predicate the predicate which to apply to the value.
   * @param value     the value which needs to be tested by the predicate.
   * @param <T>       the type of the contents.
   * @return SuccessProbable, UnprocessableProbable or UnsuccessfulProbable with the exception message.
   */
  public static <T> Probable<T> probableOf(Predicate<T> predicate, T value) {
    try {
      var probableIsValid = predicate.test(value);
      if (probableIsValid) {
        return successful(value);
      }
      return failed(Messages.UNSUCCESSFUL);
    } catch (Exception exception) {
      return failed(exception, exception.getMessage());
    }
  }

  /**
   * Transforms an existing Probable into another one while retaining the status and possibly the contents. Works as an
   * adapter. If the contents of the incoming Probable are incompatible with the expected Probable the content will be
   * lost.
   *
   * @param probable the existing Probable.
   * @param <R>    the content type of the new Probable.
   * @return Probable.
   */
  public static <R> Probable<R> transform(Probable<?> probable) {
    Type probableStatus = probable.geType();
    String probableMessage = probable.getMessage();
    Exception exception = probable.getException();
    if (probable.hasContents()) {
      R contents = orDefault(probable::get, null);

      if (contents != null) {
        return probableOf(probableStatus, probableMessage, contents, exception);
      }
    }
    return probableOf(probableStatus, probableMessage, null, exception);
  }

  private static <T> Probable<T> probableOf(Type probableType, String message, T content, Exception exception) {
    if (Objects.requireNonNull(probableType) == Type.SUCCESSFUL) {
      return successful(content);
    }
    return failed(exception, message);
  }

  private static <T> T orDefault(Supplier<?> supplier, T defaultValue) {
    try {
      return (T) supplier.get();
    } catch (Exception ignored) {
      return defaultValue;
    }
  }

  private String getLogMessage(String message) {
    return String.format(
      "%s | Probable was: %s, contents: %s, message: %s",
      message,
      geType(),
      get(),
      getMessage()
    );
  }

  /**
   * A status enum for all probables.
   */
  public enum Type {
    /**
     * Status for all successful probables.
     */
    SUCCESSFUL(Messages.SUCCESSFUL),
    /**
     * Status for all unsuccessful probables.
     */
    FAILED(Messages.UNSUCCESSFUL);

    private final String defaultMessage;

    Type(String defaultMessage) {this.defaultMessage = defaultMessage;}

    public String getDefaultMessage() {
      return defaultMessage;
    }
  }

}
