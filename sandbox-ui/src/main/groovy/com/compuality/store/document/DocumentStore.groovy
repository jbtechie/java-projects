package com.compuality.store.document

import com.compuality.query.Query

interface DocumentStore {

  Iterable<StoredDocument> findWith(Query<?> query)

  StoreResult store(List<Document> documents)
}