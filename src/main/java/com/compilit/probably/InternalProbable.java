package com.compilit.probably;

class InternalProbable<T> extends Probable<T> {

  InternalProbable(Probable<T> probable) {
    super(probable.get(), probable.getException(), probable.getMessage());
  }

  final <R> Probable<R> getDeepestNestedProbable() {
    if (hasValue()) {
      if (get() instanceof Probable<?>) {
        var probable = (Probable<?>) get();
        return new InternalProbable<>(probable).getDeepestNestedProbable();
      }
      return Probable.of(() -> (R) get());
    }
    if (hasFailed()) {
      return Probable.failure(getException(), getMessage());
    }
    return Probable.nothing();
  }
}
