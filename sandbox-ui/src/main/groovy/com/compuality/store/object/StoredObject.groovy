package com.compuality.store.object

import static com.google.common.base.Preconditions.checkNotNull

class StoredObject<T> {

  private final String id
  private final T object

  StoredObject(String id, T object) {
    this.id = checkNotNull(id, 'id')
    this.object = checkNotNull(object, 'object')
  }

  String getId() {
    return id
  }

  T getObject() {
    return object
  }
}
