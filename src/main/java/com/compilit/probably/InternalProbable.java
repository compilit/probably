package com.compilit.probably;

class InternalProbable<T> extends Probable<T> {

  InternalProbable(Probable<T> probable) {
    super(probable.getValue(), probable.getException(), probable.getMessage());
  }

  final <R> Probable<R> getDeepestNestedProbable() {
    if (hasValue()) {
      if (getValue() instanceof Probable<?>) {
        var probable = (Probable<?>) getValue();
        return new InternalProbable<>(probable).getDeepestNestedProbable();
      }
      return Probable.of(() -> (R) getValue());
    }
    if (hasFailed()) {
      return Probable.failure(getException(), getMessage());
    }
    return Probable.nothing();
  }
}
