package com.compuality.collection.impl

import com.compuality.collection.CollectionAppender
import com.google.common.collect.Iterables

import static com.google.common.base.Preconditions.checkNotNull

class CollectionAppenderImpl<T> implements CollectionAppender<T> {

  private final Collection<T> collection

  public static <T> CollectionAppender<T> listAppender() {
    return new CollectionAppenderImpl<T>(new ArrayList<T>())
  }

  CollectionAppenderImpl(Collection<T> collection) {
    this.collection = checkNotNull(collection)
  }

  @Override
  CollectionAppender add(T... elems) {
    collection.addAll(elems)
    return this
  }

  @Override
  CollectionAppender add(Iterable<T> elems) {
    Iterables.addAll(collection, elems)
    return this
  }

  Iterable<T> items() {
    return collection
  }
}
