package com.compuality.query

import com.compuality.query.Query.Constraint

class CoreConstraints {

  static <F> Constraint<F> equalTo(F value) {
    return new EqualToConstraint<F>(value)
  }

  static <F> Constraint<F> greaterThan(F value) {
    return new GreaterThanConstraint<F>(value)
  }

  static class EqualToConstraint<F> implements Constraint<F> {

    private final F value

    EqualToConstraint(F value) {
      this.value = value
    }

    F getValue() {
      return value
    }
  }

  static class GreaterThanConstraint<F> implements Constraint<F> {

    private final F value

    GreaterThanConstraint(F value) {
      this.value = value
    }

    F getValue() {
      return value
    }
  }
}
