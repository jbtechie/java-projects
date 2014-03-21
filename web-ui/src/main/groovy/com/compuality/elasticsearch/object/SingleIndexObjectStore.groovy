package com.compuality.elasticsearch.object
import com.compuality.elasticsearch.store.document.SingleIndexDocumentStore
import com.compuality.query.Query
import com.compuality.store.object.ObjectStore
import com.compuality.store.object.PreparedObject
import com.compuality.store.object.StoredObject
import com.fasterxml.jackson.databind.ObjectMapper

class SingleIndexObjectStore implements ObjectStore {

  private final SingleIndexDocumentStore documentStore
  private final ObjectMapper mapper

  SingleIndexObjectStore(SingleIndexDocumentStore documentStore, ObjectMapper mapper) {
    this.documentStore = documentStore
    this.mapper = mapper
  }

  @Override
  def <T> Iterable<StoredObject<T>> findWith(Query<T> query) {
    return null
  }

  @Override
  def <T> void store(List<PreparedObject<T>> preparedObjects) {

  }

  private static class ObjectWrapper<T> {

    private final Set<Class<?>> implementedClasses
    private final T object
  }
}
