package com.compuality.elasticsearch
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.inject.Inject
import com.google.inject.Provider
import org.elasticsearch.action.bulk.BulkItemResponse
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
    Client client = clientProvider.get()

    Observable objectAndRequests = documents.map({ [object:it, request:client.prepareIndex(index, type)
                                                                     .setSource(it)
                                                                     .setId(UUID.nameUUIDFromBytes(it.bytes).toString())] })
    bulkIndex(objectAndRequests).subscribe({ logger.debug(it.object.toString()) })
  }

  void addObjectsWithHashId(String index, String type, Observable<Object> objects) {
    addDocumentsWithHashId(index, type, objects.map({ mapper.writeValueAsString(it) }))
  }

  public Observable<BulkIndexResult> bulkIndex(Observable objectAndRequests) {
    Client client = clientProvider.get()
    return objectAndRequests.synchronize()
      .doOnError({ logger.error(it) })
      .finallyDo({ client.close() })
      .finallyDo({ logger.debug('Done') })
      .reduce([objects:[], builder:client.prepareBulk()], { result, objectAndRequest ->
        result.objects.add(objectAndRequest.object); result.builder.add(objectAndRequest.request); result
      })
      .mapMany({ result ->
        BulkResponse bulkResponse = result.builder.execute().actionGet()
        return Observable.zip(Observable.from(result.objects), Observable.from(bulkResponse.getItems()), { object, item ->
          return [object:object, response:item]
        })
      })
  }

  static class BulkIndexResult {
    String object
    BulkItemResponse reponse
  }
}
