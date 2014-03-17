package com.compuality.store.object

import com.compuality.query.Query

public interface ObjectStore {

  public <T> Iterable<StoredObject<T>> findWith(Query<T> query)

  public <T> void store(List<PreparedObject<T>> objects)
}