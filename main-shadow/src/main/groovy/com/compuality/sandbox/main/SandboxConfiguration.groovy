package com.compuality.sandbox.main

import com.compuality.BindConfiguration
import com.compuality.elasticsearch.ElasticSearchConfiguration
import com.yammer.dropwizard.config.Configuration

class SandboxConfiguration extends Configuration {

  @BindConfiguration
  ElasticSearchConfiguration elasticsearch = new ElasticSearchConfiguration()
}
