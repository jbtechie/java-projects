package com.compuality.elasticsearch

import com.yammer.metrics.annotation.Timed
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
  @Timed
  String runBenchmark() {
    return client.admin().cluster().prepareClusterStats().get().toString()
  }
}
