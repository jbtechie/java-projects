package com.compuality.elasticsearch
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.inject.Inject
import com.google.inject.Provider
import org.elasticsearch.action.bulk.BulkRequestBuilder
import org.elasticsearch.action.index.IndexRequestBuilder
import org.elasticsearch.client.Client
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import rx.Observable
import rx.util.functions.Action0
import rx.util.functions.Action1
import rx.util.functions.Func1
import rx.util.functions.Func2

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
                                                                     .setSource(it) } as Func1)

    bulkIndex(client, requests).mapMany({ Observable.from(it.getItems()) } as Func1)
  }

  void addObjects(String index, String type, Observable<Object> objects) {
    addDocuments(index, type, objects.map({ mapper.writeValueAsString(it) } as Func1))
  }

  void addDocumentsWithHashId(String index, String type, Observable<String> documents) {
    Client client = clientProvider.get()

    Observable<IndexRequestBuilder> requests = documents.map({ client.prepareIndex(index, type)
                                                                     .setSource(it)
                                                                     .setId(UUID.nameUUIDFromBytes(it.bytes).toString()) } as Func1)
    bulkIndex(client, requests).subscribe()
  }

  void addObjectsWithHashId(String index, String type, Observable<Object> objects) {
    addDocumentsWithHashId(index, type, objects.map({ mapper.writeValueAsString(it) } as Func1))
  }

  public Observable bulkIndex(Observable<Object> requests) {
    Client client = clientProvider.get()
    return requests.synchronize()
      .doOnError({ logger.error(it) } as Action1)
      .finallyDo({ client.close() } as Action0)
      .finallyDo({ logger.debug('Done') } as Action0)
      .reduce([objects:[], builder:client.prepareBulk()], { bulkResult, request ->
          bulkResult.objects.add(request); bulkResult.builder.add(request); bulkResult
      } as Func2)
      .map({ bulkResult -> bulkResult.response = bulkResult.builder.execute().actionGet(); bulkResult } as Func1)
      .mapMany({ bulkResult ->
        Observable.zip(Observable.from(bulkResult.objects), Observable.from(bulkResult.response.getItems()), { objects, items ->
          [objects:objects, items:items]
        } as Func2)
      })
  }

  static class BulkIndexResult {
    List<Object> objects = []
    BulkRequestBuilder builder
  }
}
