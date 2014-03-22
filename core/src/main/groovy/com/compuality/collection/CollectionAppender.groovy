package com.compuality.collection

public interface CollectionAppender<T> {

  CollectionAppender<T> add(T... items)

  CollectionAppender<T> add(Iterable<T> items)

  Iterable<T> items()
}
