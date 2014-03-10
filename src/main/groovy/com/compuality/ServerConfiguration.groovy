package com.compuality
import com.compuality.elasticsearch.ElasticSearchConfiguration
import com.yammer.dropwizard.config.Configuration

class ServerConfiguration extends Configuration {

  private ElasticSearchConfiguration elasticsearch = new ElasticSearchConfiguration()

  @com.compuality.Configuration
  ElasticSearchConfiguration getElasticsearch() {
    return elasticsearch
  }

  void setElasticsearch(ElasticSearchConfiguration elasticsearch) {
    this.elasticsearch = elasticsearch
  }
}
