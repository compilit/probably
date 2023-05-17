package com.compilit.resultify;

/**
 * A status enum for all results.
 */
public enum ResultType {
  /**
   * Status for all successful results.
   */
  SUCCESS(Messages.NOTHING_TO_REPORT),
  /**
   * Generic status for all unsuccessful results.
   */
  UNPROCESSABLE(Messages.UNPROCESSABLE),
  /**
   * Generic status for all unsuccessful results with authorization as root cause.
   */
  UNAUTHORIZED(Messages.UNAUTHORIZED),
  /**
   * Generic status for all unsuccessful results with 'not found' issues as root cause. Indicates a non-existent
   * resource.
   */
  NOT_FOUND(Messages.NOT_FOUND),
  /**
   * Generic status for all unsuccessful results with an actual exception as root cause.
   */
  ERROR_OCCURRED(Messages.ERROR_OCCURRED);

  private final String defaultMessage;

  ResultType(String defaultMessage) {this.defaultMessage = defaultMessage;}

  public String getDefaultMessage() {
    return defaultMessage;
  }
}
