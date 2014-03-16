package com.compuality.query

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method


class Query<T> {

  static <T> T pathGenerator(Class<T> clazz) {
    return null
  }

  static Builder<T> on(T pathGenerator) {
    return new InternalBuilder<>(pathGenerator)
  }

  static interface Builder<T> {

    public T getPathGenerator()

    public <F> JoinBuilder<T> where(F path, Constraint<F> constraint, JoinConstraint<F>... moreConstraints)
  }

  static interface JoinBuilder<T> {

    public T getPathGenerator()

    public <F> JoinBuilder<T> and(F path, Constraint<F> constraint, JoinConstraint<F>... moreConstraints)

    public <F> JoinBuilder<T> or(F path, Constraint<F> constraint, JoinConstraint<F>... moreConstraints)

    public Query<T> build()
  }

  static private class InternalBuilder<T> implements Builder<T>, JoinBuilder<T> {

    private final T pathGenerator
    private final List<Statement> statements = []

    InternalBuilder(T pathGenerator) {
      this.pathGenerator = pathGenerator
    }

    @Override
    T getPathGenerator() {
      return pathGenerator
    }

    @Override
    def <F> JoinBuilder<T> where(F path, Constraint<F> constraint, JoinConstraint<F>... moreConstraints) {
      statements.add(new Statement(StatementType.Initial, path, constraint, moreConstraints))
      return this
    }

    @Override
    def <F> JoinBuilder<T> and(F path, Constraint<F> constraint, JoinConstraint<F>... moreConstraints) {
      statements.add(new Statement(StatementType.And, path, constraint, moreConstraints))
      return this
    }

    @Override
    def <F> JoinBuilder<T> or(F path, Constraint<F> constraint, JoinConstraint<F>... moreConstraints) {
      statements.add(new Statement(StatementType.Or, path, constraint, moreConstraints))
      return this
    }

    @Override
    Query<T> build() {
      return null
    }

    private enum StatementType {
      Initial,
      And,
      Or
    }

    private static class Statement {

      private final StatementType type
      private final Object path
      private final Constraint constraint
      private final JoinConstraint[] moreConstraints

      private Statement(StatementType type, Object path, Constraint constraint, JoinConstraint[] moreConstraints) {
        this.type = type
        this.path = path
        this.constraint = constraint
        this.moreConstraints = moreConstraints
      }

      StatementType getType() {
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
    }
  }

  static interface Constraint<F> {

  }

  static interface JoinConstraint<F> {

  }

  static class PathGeneratorProxy implements InvocationHandler {

    private List<String> path = new ArrayList<>()

    @Override
    Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      if(method.returnType.isPrimitive()) {
        if(method.returnType == Void.class) {
          throw new RuntimeException('Void leaf not supported.')
        }

        //todo: handle
      }

      if(method.returnType.isArray() && method.returnType.componentType.isPrimitive()) {
        if(method.returnType.componentType == Void.class) {
          throw new RuntimeException('Array of voids leaf not supported.')
        }

        //todo: handle
      }

      if(Collection.isAssignableFrom(method.returnType)) {
        if(method.returnType.typeParameters.length != 1) {
          throw new RuntimeException('Invalid collection type.')
        }
        final Class<?> genericDeclaration = method.returnType.typeParameters[0].genericDeclaration
        if(genericDeclaration.isPrimitive()) {
          if(genericDeclaration == Void.class) {
            throw new RuntimeException('Collection of voids leaf not supported.')
          }

          //todo: handle
        }
      }

      return null
    }
  }
}
