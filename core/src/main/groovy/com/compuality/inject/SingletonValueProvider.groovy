package com.compuality.inject

import javax.inject.Provider

abstract class SingletonValueProvider<T> implements Provider<T> {

  private T value

  @Override
  final T get() {
    if(value == null) {
      synchronized(this) {
        // check again inside sync block
        if(value == null) {
          value = create()
        }
      }
    }
    return value
  }

  abstract protected T create()
}
