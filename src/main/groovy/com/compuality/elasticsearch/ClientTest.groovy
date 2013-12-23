package com.compuality.elasticsearch

import com.google.inject.Inject
import org.elasticsearch.action.index.IndexResponse
import org.elasticsearch.client.Client

class ClientTest {

  @Inject
  public ClientTest(Client client) {
//    CreateIndexResponse resp = client.admin().indices().create(Requests.createIndexRequest("experiments")).actionGet()
//    println "Index response: ${resp.acknowledged}"
//    new ObjectMapper()
    IndexResponse resp = client.prepareIndex('experiments', 'experiment').setSource([testName:'testVal']).execute().actionGet()
    println "Index response: ${resp}"
  }
}
