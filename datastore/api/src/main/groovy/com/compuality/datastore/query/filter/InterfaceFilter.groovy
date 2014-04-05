package com.compuality.datastore.query.filter

import static com.google.common.base.Preconditions.checkArgument
import static com.google.common.base.Preconditions.checkNotNull

class InterfaceFilter<T> implements TypedFilter<T> {

  private final Class<T> interfaceClass

  InterfaceFilter(Class<T> interfaceClass) {
    checkNotNull(interfaceClass)
    checkArgument(interfaceClass.interface)
    this.interfaceClass = interfaceClass
  }

  Class<T> getInterface() {
    return interfaceClass
  }
}
