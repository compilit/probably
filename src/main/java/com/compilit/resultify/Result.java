package com.compilit.resultify;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

/**
 * This interface is your entrypoint for all result operations.
 *
 * @param <T> The type of the result you wish to return.
 */
public interface Result<T> {

  /**
   * @return the ResultType of the result.
   */
  ResultType getResultType();


  /**
   * @return the message of the result.
   */
  String getMessage();


  /**
   * @return the exception that occurred in the result.
   */
  Exception getException();

  /**
   * @return true if the ResultType equals SUCCESS.
   */
  default boolean isSuccessful() {
    return getResultType().equals(ResultType.SUCCESS);
  }

  /**
   * Find out of the given result is in fact successful or not. A successful result does not imply that it has
   * contents.
   *
   * @return true if the ResultType does not equal SUCCESS.
   */
  default boolean isUnsuccessful() {
    return !isSuccessful();
  }

  /**
   * Get the contents of the result, which can in fact be null.
   *
   * @return the nullable contents of the result.
   */
  default T get() {
    return orElse(null);
  }

  /**
   * Get the contents of the result if present, otherwise return the default value.
   *
   * @return the contents of the result or the defaultValue.
   */
  default T orElse(T defaultValue) {
    if (isSuccessful()) {
      return get();
    }
    return defaultValue;
  }

  /**
   * Get the result if present, otherwise return the other Result.
   *
   * @return the alternative the result or the other Result.
   */
  default Result<T> or(Result<T> otherResult) {
    if (isSuccessful()) {
      return this;
    }
    return otherResult;
  }

  /**
   * @return true if the Result has contents.
   */
  default boolean hasContents() {
    return get() != null;
  }

  /**
   * @return true if the Result has no contents.
   */
  default boolean isEmpty() {
    return !hasContents();
  }

  /**
   * In case of a successful result, apply the mapping function to the content to return a different content type. It
   * should be noted that the result status "SUCCESS" does not automatically mean the result has contents. Which is why
   * the result of the thenApply function can be influenced by the mapping function that is passed.
   *
   * @param mappingFunction, the operation you wish to apply to the contents
   * @param <R>,             the return type
   * @return the final Result
   */
  default <R> Result<R> map(Function<? super T, ? extends R> mappingFunction) {
    if (isSuccessful()) {
      var functionResult = mappingFunction.apply(get());
      if (functionResult instanceof Result<?>) {
        return (Result<R>) functionResult;
      }
      return resultOf(() -> functionResult);
    }
    return transform(this);
  }

  /**
   * Transform a Result of T1 to a Result of T2 Since the return type can be different at compile-time, but the same at
   * runtime, this method will allow you to continue your stream as planned.
   *
   * @param <R>, the return type
   * @return the final Result
   */
  default <R> Result<R> transform() {
    return transform(this);
  }

  /**
   * In case of a successful result, apply the mapping function to the content to return a different content type. It
   * should be noted that the result status "SUCCESS" does not automatically mean the result has contents. Which is why
   * the result of the thenApply function can be influenced by the mapping function that is passed.
   *
   * @param mappingFunction, the operation you wish to apply to the contents
   * @param <R>,             the return type
   * @return the final Result
   */
  default <R> Result<R> flatMap(Function<? super T, ? extends Result<? extends R>> mappingFunction) {
    if (isSuccessful()) {
      return (Result<R>) mappingFunction.apply(get());
    }
    return transform(this);
  }

  /**
   * Validate the contents of the result. The original ResultType is ignored unless the contents complies with the given
   * predicate.
   *
   * @param predicate the validation you wish to perform on the contents
   * @return the result if it complies with the given predicate, otherwise the result with the altered ResultType
   */
  default Result<T> test(Predicate<T> predicate) {
    return test(predicate, ResultType.UNPROCESSABLE);
  }

  /**
   * Validate the contents of the result. The original ResultType is ignored unless the contents complies with the given
   * predicate.
   *
   * @param predicate the validation you wish to perform on the contents
   * @return the result if it complies with the given predicate, otherwise the result with the altered ResultType
   */
  default Result<T> test(Predicate<T> predicate, ResultType onInvalid) {
    boolean isValid = orDefault(() -> predicate.test(get()), false);
    if (isValid) {
      return this;
    }
    return resultOf(onInvalid, onInvalid.getDefaultMessage(), get(), null);
  }

  /**
   * Log the result on level INFO
   *
   * @return the original result, or the altered result in case of an exception
   */
  default Result<T> log() {
    return log(Level.INFO);
  }

  /**
   * Log the message and result on level INFO
   *
   * @param message the message you wish to log before the result log
   * @return the original result, or the altered result in case of an exception
   */
  default Result<T> log(String message) {
    return log(Level.INFO, message);
  }

  /**
   * Log the result on the provided level
   *
   * @param level the level you wish the log the message on
   * @return the original result, or the altered result in case of an exception
   */
  default Result<T> log(Level level) {
    return log(level, "Result processed");
  }

  /**
   * Log the result and message on the provided level
   *
   * @param level   the level you wish the log the message on
   * @param message the message you wish to log before the result log
   * @return the original result, or the altered result in case of an exception
   */
  default Result<T> log(Level level, String message) {
    String resultMessage = getLogMessage(message);
    var exception = getException();
    if (exception != null) {
      LoggerFactory.getLogger(getClass()).atLevel(level).log(resultMessage, exception);
    } else {
      LoggerFactory.getLogger(getClass()).atLevel(level).log(resultMessage);
    }
    return this;
  }

  /**
   * A generic success result for a process or validation.
   *
   * @param <T> the type of the contents.
   * @return a success Result.
   */
  static <T> Result<T> success() {
    return new SuccessResult<>();
  }

  /**
   * A generic success result for a process or validation.
   *
   * @param contents the contents of the result.
   * @param <T>      the type of the contents.
   * @return a success Result with contents. Or an empty resource Result if the content is null.
   */
  static <T> Result<T> success(T contents) {
    return new SuccessResult<>(contents);
  }

  /**
   * A generic result for when the client asks for a non-existent resource.
   *
   * @param <T> the content type.
   * @return a not found Result without a message.
   */
  static <T> Result<T> notFound() {
    return new NotFoundResult<>();
  }

  /**
   * A generic result for when the client asks for a non-existent resource.
   *
   * @param <T>             the content type.
   * @param message         the message you wish to propagate.
   * @param formatArguments the message arguments you with to replace the '%s' (for example) symbol with.
   * @return a not found Result with a message.
   */
  static <T> Result<T> notFound(String message, String... formatArguments) {
    var actualMessage = MessageFormatter.formatMessage(message, (Object[]) formatArguments);
    return new NotFoundResult<>(actualMessage);
  }

  /**
   * A generic failure result. Can be used for pretty much any failed process or validation.
   *
   * @param <T> the type of the contents.
   * @return an unprocessable Result without a message.
   */
  static <T> Result<T> unprocessable() {
    return new UnprocessableResult<>();
  }

  /**
   * A generic failure result. Can be used for pretty much any failed process or validation.
   *
   * @param message         the message you wish to propagate.
   * @param formatArguments the message arguments you with to replace the '%s' (for example) symbol with.
   * @param <T>             the type of the contents.
   * @return an unprocessable Result with a message.
   */
  static <T> Result<T> unprocessable(String message, String... formatArguments) {
    var actualMessage = MessageFormatter.formatMessage(message, (Object[]) formatArguments);
    return new UnprocessableResult<>(actualMessage);
  }

  /**
   * A generic result for any encountered authentication/authorization issue.
   *
   * @param <T> the content type.
   * @return an empty unauthorized Result without a message.
   */
  static <T> Result<T> unauthorized() {
    return new UnauthorizedResult<>();
  }

  /**
   * A generic result for any encountered authentication/authorization issue.
   *
   * @param message         the message you wish to propagate.
   * @param formatArguments the message arguments you with to replace the '%s' (for example) symbol with.
   * @param <T>             the content type.
   * @return an empty unauthorized Result with a message.
   */
  static <T> Result<T> unauthorized(String message, String... formatArguments) {
    var actualMessage = MessageFormatter.formatMessage(message, (Object[]) formatArguments);
    return new UnauthorizedResult<>(actualMessage);
  }

  /**
   * A generic result for any encountered exceptions.
   *
   * @param <T>             the content type.
   * @param message         the error message.
   * @param formatArguments the message arguments you with to replace the '%s' (for example) symbol with.
   * @return an error occurred Result with a message.
   */
  static <T> Result<T> errorOccurred(String message, String... formatArguments) {
    var actualMessage = MessageFormatter.formatMessage(message, (Object[]) formatArguments);
    return new ErrorOccurredResult<>(actualMessage);
  }

  /**
   * A generic result for any encountered exceptions.
   *
   * @param <T>             the content type.
   * @param message         the error message.
   * @param formatArguments the message arguments you with to replace the '%s' (for example) symbol with.
   * @return an error occurred Result with a message.
   */
  static <T> Result<T> errorOccurred(Exception exception, String message, String... formatArguments) {
    var actualMessage = MessageFormatter.formatMessage(message, (Object[]) formatArguments);
    return new ErrorOccurredResult<>(exception, actualMessage);
  }

  /**
   * A generic result that encapsulates a supplying process. Returns a Success result with the supplied content if the
   * Supplier does not throw an Exception. If the Supplier throws an Exception, it returns a corresponding result with
   * the exception message added to the Result.
   *
   * @param resultUnaryOperator the function that returns the final result.
   * @param <T>                 the type of the contents.
   * @return SuccessResult or ErrorOccurredResult with the exception message.
   */
  static <T> Result<T> resultOf(UnaryOperator<Result<T>> resultUnaryOperator) {
    var defaultResult = Result.<T>success(null);
    try {
      return resultUnaryOperator.apply(defaultResult);
    } catch (Exception exception) {
      return errorOccurred(exception, exception.getMessage());
    }
  }

  /**
   * A generic result that encapsulates a supplying process. Returns a Success result with the supplied content if the
   * Supplier does not throw an Exception. If the Supplier throws an Exception, it returns a corresponding result with
   * the exception message added to the Result.
   *
   * @param supplier the content-supplying function.
   * @param <T>      the type of the contents.
   * @return SuccessResult or ErrorOccurredResult with the exception message.
   */
  static <T> Result<T> resultOf(Supplier<T> supplier) {
    try {
      var tmp = supplier.get();
      if (tmp instanceof Result<?>) {
        return ((Result<?>) tmp).transform();
      }
      return success(supplier.get());
    } catch (Exception exception) {
      return errorOccurred(exception, exception.getMessage());
    }
  }

  /**
   * A generic result that encapsulates a predicate. Returns a Success result with the value if the predicate resolves
   * to true. And an Unprocessable result if it resolves to false. If the Predicate throws an Exception, it returns an
   * ErrorOccurredResult with the exception message added to the Result.
   *
   * @param predicate the predicate which to apply to the value.
   * @param value     the value which needs to be tested by the predicate.
   * @param <T>       the type of the contents.
   * @return SuccessResult, UnprocessableResult or ErrorOccurredResult with the exception message.
   */
  static <T> Result<T> resultOf(Predicate<T> predicate, T value) {
    try {
      var result = predicate.test(value);
      if (result) {
        return Result.success(value);
      }
      return Result.unprocessable();
    } catch (Exception exception) {
      return errorOccurred(exception, exception.getMessage());
    }
  }

  /**
   * Transforms an existing Result into another one while retaining the status and possibly the contents. Works as an
   * adapter. If the contents of the incoming Result are incompatible with the expected Result the content will be
   * lost.
   *
   * @param result the existing Result.
   * @param <R>    the content type of the new Result.
   * @return Result.
   */
  static <R> Result<R> transform(Result<?> result) {
    ResultType resultStatus = result.getResultType();
    String resultMessage = result.getMessage();
    Exception exception = result.getException();
    if (result.hasContents()) {
      R contents = orDefault(result::get, null);

      if (contents != null) {
        return resultOf(resultStatus, resultMessage, contents, exception);
      }
    }
    return resultOf(resultStatus, resultMessage, null, exception);
  }

  private static <T> Result<T> resultOf(ResultType resultType, String message, T content, Exception exception) {
    switch (resultType) {
      case SUCCESS:
        return success(content);
      case UNAUTHORIZED:
        return unauthorized(message);
      case NOT_FOUND:
        return notFound(message);
      case ERROR_OCCURRED:
        return errorOccurred(exception, message);
      default:
        return unprocessable(message);
    }
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
      "%s | Result was: %s, contents: %s, message: %s",
      message,
      getResultType(),
      get(),
      getMessage()
    );
  }

}
