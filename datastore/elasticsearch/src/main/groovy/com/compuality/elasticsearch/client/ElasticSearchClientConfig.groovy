package com.compuality.elasticsearch.client

import com.compuality.resource.ResourceUtils

class ElasticSearchClientConfig {

  private static final String DEFAULT_RESOURCE_CONFIG = ResourceUtils.getResource(ElasticSearchClientConfig, 'elasticsearch.yml')

  String nativeConfigResource = DEFAULT_RESOURCE_CONFIG

  ElasticSearchClientConfig() {
  }

  ElasticSearchClientConfig(String nativeConfigResource) {
    this.nativeConfigResource = nativeConfigResource
  }
}
