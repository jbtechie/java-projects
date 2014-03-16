package com.compuality.elasticsearch.store.document

import com.compuality.query.Query
import com.compuality.store.document.Document
import com.compuality.store.document.DocumentStore
import com.compuality.store.document.StoreResult
import com.compuality.store.document.StoredDocument

class ElasticSearchDocumentStore implements DocumentStore {

  @Override
  Iterable<StoredDocument> findWith(Query<?> query) {
    return null
  }

  @Override
  StoreResult store(List<Document> documents) {
    return null
  }

  enum PartitionStrategy {
    SingleIndex,
    IndexPerType,
    IndexPerCreatedGroup,
    IndexPerPartition
  }

  static class Configuration {

    PartitionStrategy partitionStrategy
    int shards
    int replicas
  }
}
