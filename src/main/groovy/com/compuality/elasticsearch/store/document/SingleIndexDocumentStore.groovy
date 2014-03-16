package com.compuality.elasticsearch.store.document
import com.compuality.query.Query
import com.compuality.store.document.Document
import com.compuality.store.document.DocumentStore
import com.compuality.store.document.StoreResult
import com.compuality.store.document.StoredDocument
import com.google.common.collect.Iterators
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesResponse
import org.elasticsearch.action.bulk.BulkRequestBuilder
import org.elasticsearch.action.bulk.BulkResponse
import org.elasticsearch.client.Client
import org.elasticsearch.cluster.metadata.IndexMetaData

class SingleIndexDocumentStore implements DocumentStore {

  private final Client client
  private final Configuration config
  private final MappingGenerator mapper

  private int indexVersion = 0;
  private String writeIndex

  private final Set<String> mappedTypes = new HashSet<>()

  SingleIndexDocumentStore(Client client, Configuration config, MappingGenerator mapper) {
    this.client = client
    this.config = config
    this.mapper = mapper

    GetAliasesResponse getAliasesResponse = client.admin().indices().prepareGetAliases(config.namespace).get()
    if(getAliasesResponse.aliases.empty) {
      writeIndex = StoreUtil.appendVersion(config.namespace, indexVersion)
      client.admin().indices().prepareCreate(writeIndex).get()
      client.admin().indices().prepareAliases().addAlias(writeIndex, config.namespace).get()
    } else {
      if(getAliasesResponse.aliases.keys().size() != 1) {
        throw new RuntimeException('Single index document store must only alias a single index at a time.')
      }

      writeIndex = getAliasesResponse.aliases.keys().first()
      indexVersion = StoreUtil.parseVersion(writeIndex)

      IndexMetaData indexMetadata = client.admin().cluster().prepareState().setIndices(writeIndex).get()
          .state.metaData.index(writeIndex)
      Iterators.addAll(mappedTypes, indexMetadata.mappings.keysIt())
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
      if(!mappedTypes.contains(doc.type)) {
        synchronized(mappedTypes) {
          if(!mappedTypes.contains(doc.type)) {
            final String mapping = mapper.map(doc)
            client.admin().indices().preparePutMapping(writeIndex).setType(doc.type).setSource(mapping).get()
            mappedTypes.add(doc.type)
          }
        }
      }

      bulkRequest.add(client.prepareIndex(writeIndex, doc.type, doc.id.orNull()).setSource(doc.content))
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
