package com.compuality.elasticsearch

import com.google.common.base.Optional
import com.yammer.metrics.annotation.Timed
import org.elasticsearch.client.Client
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.inject.Inject
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

@Path('elasticsearch')
@Consumes(MediaType.APPLICATION_JSON)
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
  String runBenchmark(@QueryParam('name') Optional<String> name) {
    if(name.isPresent()) {
      return '["' + name.get() + '"]'
    } else {
      return '{"name":"no name given"}'
    }
  }
}
