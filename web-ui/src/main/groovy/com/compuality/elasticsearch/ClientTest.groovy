package com.compuality.elasticsearch
import com.google.inject.Inject
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse
import org.elasticsearch.client.Client
import org.elasticsearch.client.Requests
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ClientTest {

  private static final Logger logger = LoggerFactory.getLogger(ClientTest)

  @Inject
  public ClientTest(Client client) {
    try {
      CreateIndexResponse resp = client.admin().indices().create(Requests.createIndexRequest("experiments")).actionGet()
      logger.debug("Acknowledged: ${resp.acknowledged}")
    } catch(Exception e) {}
//    println "Index response: ${resp.acknowledged}"
//    new ObjectMapper()
//    IndexResponse resp = client.prepareIndex('experiments', 'experiment').setSource([testName:'testVal']).execute().actionGet()
//    println "Index response: ${resp}"
  }
}
