package com.compuality.sandbox.main

import com.compuality.elasticsearch.client.ElasticSearchClientConfig
import com.compuality.inject.Bind
import com.compuality.resource.ResourceUtils
import com.yammer.dropwizard.config.Configuration

class SandboxConfiguration extends Configuration {

  @Bind
  ElasticSearchClientConfig elasticsearch = new ElasticSearchClientConfig(ResourceUtils.getResource(SandboxConfiguration, 'elasticsearch.yml'))
}
