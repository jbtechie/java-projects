package com.compuality.elasticsearch
import com.compuality.elasticsearch.util.TestNodeFactory
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesResponse
import org.elasticsearch.client.Client
import org.elasticsearch.node.Node
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test

import static org.hamcrest.CoreMatchers.equalTo
import static org.junit.Assert.assertThat

class ElasticSearchLearningTests {

  private static Node node
  private static Client client

  @BeforeClass
  static void setup() {
    node = TestNodeFactory.create()
    client = node.client()
  }

  @AfterClass
  static void cleanup() {
    node.close()
  }

  @Test
  void testAliasesExists() {
    String index = 'test_index'
    String alias = 'test_alias'
    client.admin().indices().prepareCreate(index).get()
    client.admin().indices().prepareAliases().addAlias(index, alias).get()
    GetAliasesResponse response = client.admin().indices().prepareGetAliases().get()
    int count = response.aliases.keys().size()
    assertThat(count, equalTo(1))
    assertThat(response.aliases.containsKey(index), equalTo(true))
  }
}
