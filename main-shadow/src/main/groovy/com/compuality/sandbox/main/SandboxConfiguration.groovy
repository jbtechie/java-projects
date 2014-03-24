package com.compuality.sandbox.main

import com.compuality.guice.Bind
import com.compuality.elasticsearch.ElasticSearchConfiguration
import com.yammer.dropwizard.config.Configuration

class SandboxConfiguration extends Configuration {

  @Bind
  ElasticSearchConfiguration elasticsearch = new ElasticSearchConfiguration()
}
