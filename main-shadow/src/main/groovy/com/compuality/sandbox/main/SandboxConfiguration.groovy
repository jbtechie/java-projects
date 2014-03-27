package com.compuality.sandbox.main

import com.compuality.guice.Bind
import com.compuality.elasticsearch.ElasticSearchConfig
import com.yammer.dropwizard.config.Configuration

class SandboxConfiguration extends Configuration {

  @Bind
  ElasticSearchConfig elasticsearch = new ElasticSearchConfig()
}
