package com.compuality.elasticsearch.store.document
import com.compuality.query.Query
import com.compuality.store.document.Document
import com.compuality.store.document.DocumentStore
import com.compuality.store.document.StoreResult
import com.compuality.store.document.StoredDocument
import com.google.common.base.Joiner
import com.google.common.base.Splitter
import com.google.common.collect.Iterators
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesResponse
import org.elasticsearch.action.bulk.BulkRequestBuilder
import org.elasticsearch.action.bulk.BulkResponse
import org.elasticsearch.client.Client

class IndexPerTypeDocumentStore implements DocumentStore {

  private static final Joiner INDEX_JOINER = Joiner.on('.')
  private static final Splitter INDEX_SPLITTER = Splitter.on('.')

  private final Client client
  private final Configuration config
  private final MappingGenerator mapper

  private int storeVersion = 0;

  private final Map<String, String> indexedTypes = new HashMap<>()

  IndexPerTypeDocumentStore(Client client, Configuration config, MappingGenerator mapper) {
    this.client = client
    this.config = config
    this.mapper = mapper

    GetAliasesResponse getAliasesResponse = client.admin().indices().prepareGetAliases(config.namespace).get()
    if(!getAliasesResponse.aliases.empty) {
      final String[] indices = Iterators.toArray(getAliasesResponse.aliases.keysIt(), String)

      for(String index : indices) {
        Iterator<String> parts = INDEX_SPLITTER.split(index.substring(config.namespace.length()+1)).iterator()
        storeVersion = Integer.parseInt(parts.next())
        String type = parts.next()
        indexedTypes.put(type, index)
      }
    }
  }

  @Override
  Iterable<StoredDocument> findWith(Query<?> query) {
    return null
  }

  @Override
  StoreResult store(List<Document> documents) {
    BulkRequestBuilder bulkRequest = client.prepareBulk()

    for(Document doc : documents) {
      if(!indexedTypes.containsKey(doc.type)) {
        synchronized(indexedTypes) {
          if(!indexedTypes.containsKey(doc.type)) {
            final String index = INDEX_JOINER.join(config.namespace, storeVersion, doc.type)
            final String mapping = mapper.map(doc)
            client.admin().indices().prepareCreate(index).addMapping(doc.type, mapping).get()
            indexedTypes.put(doc.type, index)
          }
        }
      }

      bulkRequest.add(client.prepareIndex(indexedTypes.get(doc.type), doc.type, doc.id.orNull()).setSource(doc.content))
    }

    BulkResponse bulkResponse = bulkRequest.get()
    boolean allSuccessful = !bulkResponse.hasFailures()

    return new StoreResult(allSuccessful, null)
  }

  static class Configuration {

    String namespace
    int shards
    int replicas
  }
}
