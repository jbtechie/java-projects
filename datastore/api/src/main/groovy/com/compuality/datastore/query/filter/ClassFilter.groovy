package com.compuality.datastore.query.filter

class ClassFilter<T> implements TypedFilter<T> {

  private final Class<T> objectClass

  ClassFilter(Class<T> objectClass) {
    this.objectClass = objectClass
  }

  Class<T> getObjectClass() {
    return objectClass
  }
}
