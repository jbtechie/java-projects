package com.compuality.datastore

import com.compuality.datastore.query.filter.Filter
import com.compuality.datastore.query.filter.TypedFilter
import com.compuality.interfaces.Identified

interface Datastore {

  public Iterable<IdentifiedObject<Object>> find(Filter query)

  public <T> Iterable<IdentifiedObject<T>> findTyped(TypedFilter<T> query)

  public <T> void store(List<T> objects)

  public <T> void update(List<IdentifiedObject<T>> objects)

  public <T> void delete(List<Identified> ids)
}
