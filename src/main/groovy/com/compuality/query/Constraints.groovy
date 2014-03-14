package com.compuality.query

import com.compuality.query.Query.Constraint

class Constraints {

  static <F> Constraint<F> equalTo(F value) {
    return new EqualToConstraint<F>(value)
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
}
