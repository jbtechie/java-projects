package com.compuality
import com.compuality.elasticsearch.ElasticSearchConfiguration
import com.yammer.dropwizard.config.Configuration

class ServerConfiguration extends Configuration {

  @com.compuality.Configuration
  ElasticSearchConfiguration elasticsearch = new ElasticSearchConfiguration()
}
