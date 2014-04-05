package com.compuality.datastore.impl

import com.compuality.datastore.IdentifiedObject

class IdentifiedObjectImpl<T> implements IdentifiedObject<T> {

  private final String id
  private final T object

  IdentifiedObjectImpl(String id, T object) {
    this.id = id
    this.object = object
  }

  @Override
  String getId() {
    return id
  }

  @Override
  T getObject() {
    return object
  }
}
