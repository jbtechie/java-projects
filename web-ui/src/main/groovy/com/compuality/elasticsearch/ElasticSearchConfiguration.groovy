package com.compuality.elasticsearch
import com.google.common.collect.Lists
import org.hibernate.validator.constraints.NotEmpty

class ElasticSearchConfiguration {

  @NotEmpty
  String clusterName = 'elasticsearch'

  @NotEmpty
  List<String> transportAddresses = Lists.asList('localhost:9300')
}
