package com.compuality.datastore.query.filter

class Filters {

  static <T> TypedAndFilter<T> typedAnd(Filter... filters) {
    return new TypedAndFilter<T>(filters)
  }

  static <T> TypedAndFilter<T> typedAnd(TypedFilter<T>... filters) {
    return new TypedAndFilter<T>(filters)
  }

  static <T> TypedOrFilter<T> typedOr(Filter... filters) {
    return new TypedOrFilter<T>(filters)
  }

  static <T> TypedOrFilter<T> typedOr(TypedFilter<T>... filters) {
    return new TypedOrFilter<T>(filters)
  }

  static <T> ClassFilter<T> isClass(Class<T> objectClass) {
    return new ClassFilter<T>(objectClass)
  }

  static <T> InterfaceFilter<T> implementsInterface(Class<T> objectInterface) {
    return new InterfaceFilter<T>(objectInterface)
  }

  static <T> AndFilter and(Filter... filters) {
    return new AndFilter(filters)
  }

  static <T> OrFilter or(Filter... filters) {
    return new OrFilter(filters)
  }

  static FieldValuesFilter hasFieldValues(String fieldPath, Object... values) {
    return new FieldValuesFilter(fieldPath, values)
  }
}
