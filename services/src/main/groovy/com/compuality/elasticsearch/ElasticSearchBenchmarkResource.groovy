package com.compuality.elasticsearch

import com.compuality.inject.WebService
import com.google.common.base.Optional
import com.yammer.metrics.annotation.Timed
import org.elasticsearch.client.Client
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.core.MediaType

@Path('elasticsearch')
@Produces(MediaType.APPLICATION_JSON)
class ElasticSearchBenchmarkResource implements WebService {

  private static final Logger log = LoggerFactory.getLogger(ElasticSearchBenchmarkResource)

  private final Client client

  @Inject
  ElasticSearchBenchmarkResource(Client client) {
    this.client = client
  }

  @GET
  @Timed
  User runBenchmark(@QueryParam('name') Optional<String> name) {
    if(name.isPresent()) {
      return new User(name:name.get())
    } else {
      return 'No name given.'
    }
  }

  static class User {
    String name
  }
}
