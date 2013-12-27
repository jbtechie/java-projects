package com.compuality.elasticsearch

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.inject.Inject
import com.google.inject.Provider
import org.elasticsearch.action.bulk.BulkItemResponse
import org.elasticsearch.action.bulk.BulkRequestBuilder
import org.elasticsearch.action.bulk.BulkResponse
import org.elasticsearch.action.index.IndexRequestBuilder
import org.elasticsearch.client.Client
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import rx.Observable

class ElasticSearchDAO {

  private static Logger logger = LoggerFactory.getLogger(ElasticSearchDAO)

  private Provider<Client> clientProvider
  private ObjectMapper mapper

  @Inject
  ElasticSearchDAO(Provider<Client> clientProvider, ObjectMapper mapper) {
    this.clientProvider = clientProvider
    this.mapper = mapper
  }

  void addDocuments(String index, String type, Observable<String> documents) {
    Client client = clientProvider.get()

    Observable<IndexRequestBuilder> requests = documents.map({ client.prepareIndex(index, type)
                                                                     .setSource(it) })

    bulkIndex(requests).mapMany({ Observable.from(it.getItems()) })
  }

  void addObjects(String index, String type, Observable<Object> objects) {
    addDocuments(index, type, objects.map({ mapper.writeValueAsString(it) }))
  }

  void addDocumentsWithHashId(String index, String type, Observable<String> documents) {
    bulkIndex(index, type, documents).subscribe({ logger.debug(it.object.toString()) })
  }

  void addObjectsWithHashId(String index, String type, Observable<Object> objects) {
    addDocumentsWithHashId(index, type, objects.map({ mapper.writeValueAsString(it) }))
  }

  public Observable<BulkIndexResult> bulkIndex(String index, String type, Observable<String> documents) {
    Client client = clientProvider.get()
    return documents.synchronize()
      .doOnError({ logger.error(it) })
      .finallyDo({ client.close() })
      .finallyDo({ logger.debug('Done') })
      .buffer(1000)
      .mapMany({ documentBuffer ->
        BulkRequestBuilder bulkBuilder = client.prepareBulk()
        documentBuffer.each {
          bulkBuilder.add(client.prepareIndex(index, type)
                                .setSource(it)
                                .setId(UUID.nameUUIDFromBytes(it.bytes).toString()))
        }
        BulkResponse bulkResponse = bulkBuilder.execute().actionGet()
        return Observable.zip(Observable.from(documentBuffer), Observable.from(bulkResponse.getItems()), { object, item ->
          return [object:object, response:item]
        })
      })
  }

  static class BulkIndexResult {
    String object
    BulkItemResponse reponse
  }
}
