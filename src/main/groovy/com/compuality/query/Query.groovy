package com.compuality.query

import com.compuality.query.Query.InternalBuilder.Statement.Type

class Query<T> {

  static interface Builder<T> {

    public <F> JoinBuilder<T> where(F path, Constraint<F> constraint, JoinConstraint<F>... moreConstraints)
  }

  static interface JoinBuilder<T> {

    public <F> JoinBuilder<T> and(F path, Constraint<F> constraint, JoinConstraint<F>... moreConstraints)

    public <F> JoinBuilder<T> or(F path, Constraint<F> constraint, JoinConstraint<F>... moreConstraints)

    public Query<T> build()
  }

  static private class InternalBuilder implements Builder<T>, JoinBuilder<T> {

    private List<Statement> statements = []

    @Override
    def <F> JoinBuilder<T> where(F path, Constraint<F> constraint, JoinConstraint<F>... moreConstraints) {
      statements.add(new Statement(Type.Initial, path, constraint, moreConstraints))
      return this
    }

    @Override
    def <F> JoinBuilder<T> and(F path, Constraint<F> constraint, JoinConstraint<F>... moreConstraints) {
      statements.add(new Statement(Type.And, path, constraint, moreConstraints))
      return this
    }

    @Override
    def <F> JoinBuilder<T> or(F path, Constraint<F> constraint, JoinConstraint<F>... moreConstraints) {
      statements.add(new Statement(Type.Or, path, constraint, moreConstraints))
      return this
    }

    @Override
    Query<T> build() {
      return null
    }

    private static class Statement {

      private final Type type
      private final Object path
      private final Constraint constraint
      private final JoinConstraint[] moreConstraints

      private Statement(Type type, Object path, Constraint constraint, JoinConstraint[] moreConstraints) {
        this.type = type
        this.path = path
        this.constraint = constraint
        this.moreConstraints = moreConstraints
      }

      Type getType() {
        return type
      }

      Object getPath() {
        return path
      }

      Constraint getConstraint() {
        return constraint
      }

      JoinConstraint[] getMoreConstraints() {
        return moreConstraints
      }

      enum Type {
        Initial,
        And,
        Or
      }
    }
  }

  static interface Constraint<F> {

  }

  static interface JoinConstraint<F> {

  }
}
