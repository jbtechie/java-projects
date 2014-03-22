package com.compuality.elasticsearch

import com.fasterxml.jackson.databind.SerializationFeature
import com.yammer.dropwizard.json.ObjectMapperFactory
import org.elasticsearch.action.admin.cluster.stats.ClusterStatsResponse
import org.elasticsearch.client.Client
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path('/elasticsearch')
@Produces(MediaType.APPLICATION_JSON)
class ElasticSearchBenchmarkResource {

  private static final Logger log = LoggerFactory.getLogger(ElasticSearchBenchmarkResource)

  private final Client client

  @Inject
  ElasticSearchBenchmarkResource(Client client) {
    this.client = client
  }

  @GET
  String runBenchmark() {
    ClusterStatsResponse response = client.admin().cluster().prepareClusterStats().get()
    log.debug('Response: {}', response)
    ObjectMapperFactory mapperFactory = new ObjectMapperFactory()
    mapperFactory.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
    try {
      String responseString = mapperFactory.build().writeValueAsString(response)
      log.debug('Response string: {}', responseString)
      return responseString
    } catch(Exception e) {
      log.error('Exception serializing response to JSON.', e)
      return ''
    }
  }
}
