package com.compuality.elasticsearch

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.inject.Inject
import com.google.inject.Provider
import org.elasticsearch.action.bulk.BulkItemResponse
import org.elasticsearch.action.bulk.BulkRequestBuilder
import org.elasticsearch.action.bulk.BulkResponse
import org.elasticsearch.client.Client
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import rx.Observable
import rx.util.functions.Func1

class ElasticSearchDAO {

  private static Logger logger = LoggerFactory.getLogger(ElasticSearchDAO)

  private Provider<Client> clientProvider
  private ObjectMapper mapper

  @Inject
  ElasticSearchDAO(Provider<Client> clientProvider, ObjectMapper mapper, ElasticSearchConfiguration config) {
    this.clientProvider = clientProvider
    this.mapper = mapper
  }

  Observable<BulkItemResponse> addDocuments(String index, String type, Observable<String> documents) {
    return bulkIndex(index, type, RANDOM_ID_FUNC, documents).map({ it.bulkItemResponse })
  }

  Observable<BulkItemResponse> addObjects(String index, String type, Observable<Object> objects) {
    return bulkIndex(index, type, RANDOM_ID_FUNC, objects.map({ mapper.writeValueAsString(it) })).map({ it.bulkItemResponse })
  }

  Observable<BulkItemResponse> addDocumentsWithHashId(String index, String type, Observable<String> documents) {
    return bulkIndex(index, type, HASH_ID_FUNC, documents).map({ it.bulkItemResponse })
  }

  Observable<BulkItemResponse> addObjectsWithHashId(String index, String type, Observable<Object> objects) {
    return bulkIndex(index, type, HASH_ID_FUNC, objects.map({ mapper.writeValueAsString(it) })).map({ it.bulkItemResponse })
  }

  private static class BulkIndexResult {
    String document
    BulkItemResponse bulkItemResponse
  }

  private Observable<BulkIndexResult> bulkIndex(String index, String type, Func1<String, UUID> idFunc, Observable<String> documents) {
    Client client = clientProvider.get()
    return documents.buffer(1000)
      .mapMany({ documentBuffer ->
        BulkRequestBuilder bulkBuilder = client.prepareBulk()
        documentBuffer.each {
          bulkBuilder.add(client.prepareIndex(index, type)
                                .setSource(it)
                                .setId(idFunc.call(it).toString()))
        }
        BulkResponse bulkResponse = bulkBuilder.execute().actionGet()
        return Observable.zip(Observable.from(documentBuffer), Observable.from(bulkResponse.getItems()), { d, i ->
          return [document:d, bulkItemResponse:i]
        })
      })
      .doOnError({ logger.error("Error performing bulk index.", it) })
      .finallyDo({ client.close() })
      .finallyDo({ logger.debug("Done performing bulk index.") })
  }

  private static final RANDOM_ID_FUNC = { UUID.randomUUID() }
  private static final HASH_ID_FUNC = { UUID.nameUUIDFromBytes(it.bytes) }
}
