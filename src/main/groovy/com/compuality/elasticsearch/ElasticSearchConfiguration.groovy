package com.compuality.elasticsearch

import org.hibernate.validator.constraints.NotEmpty

class ElasticSearchConfiguration {

  @NotEmpty
  String configFile = 'elasticsearch.yml'
}
