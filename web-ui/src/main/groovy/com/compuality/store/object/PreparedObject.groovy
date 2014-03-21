package com.compuality.store.object

import com.google.common.base.Optional

import static com.google.common.base.Preconditions.checkNotNull

class PreparedObject<T> {

  private final Optional<String> id
  private final T object

  PreparedObject(T object) {
    this.id = Optional.absent()
    this.object = checkNotNull(object, 'object')
  }

  PreparedObject(String id, T object) {
    this.id = Optional.of(id)
    this.object = checkNotNull(object, 'object')
  }

  Optional<String> getId() {
    return id
  }

  T getObject() {
    return object
  }
}
