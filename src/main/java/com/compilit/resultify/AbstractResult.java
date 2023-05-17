package com.compilit.resultify;

import java.util.Objects;
import java.util.Optional;

abstract class AbstractResult<T> implements Result<T> {

  private final T contents;
  private ResultType resultType;
  private String message;
  private Exception exception;

  AbstractResult(ResultType resultType) {
    this.resultType = resultType;
    this.contents = null;
    this.message = Messages.NOTHING_TO_REPORT;
  }

  AbstractResult(ResultType resultType, String message) {
    this.resultType = resultType;
    this.contents = null;
    this.message = message;
  }

  AbstractResult(ResultType resultType, Exception exception, String message) {
    this.resultType = resultType;
    this.contents = null;
    this.exception = exception;
    this.message = message;
  }

  AbstractResult(ResultType resultType, T contents) {
    this.resultType = resultType;
    this.contents = contents;
    this.message = Messages.NOTHING_TO_REPORT;
  }

  @Override
  public ResultType getResultType() {
    return resultType;
  }

  @Override
  public T get() {
    return contents;
  }

  @Override
  public String getMessage() {
    return message;
  }

  @Override
  public Exception getException() {
    return exception;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return get() == null;
    }
    if (obj instanceof Result<?> result) {
      return Objects.equals(get(), result.get());
    }
    if (hasContents() && obj.getClass().isAssignableFrom(get().getClass())) {
      return Objects.equals(get(),  obj);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Optional.ofNullable(get()).map(Object::hashCode).orElse(0);
  }

  @Override
  public String toString() {
    return String.valueOf(get());
  }
}
