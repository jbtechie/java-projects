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

  private static final Random rand = new Random()
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
    String index = randIndex()
    String alias = randAlias()
    client.admin().indices().prepareCreate(index).get()
    client.admin().indices().prepareAliases().addAlias(index, alias).get()
    GetAliasesResponse response = client.admin().indices().prepareGetAliases().get()
    int count = response.aliases.keys().size()
    assertThat(count, equalTo(1))
    assertThat(response.aliases.containsKey(index), equalTo(true))
  }

  @Test
  void testGetAliasesWhenNonExistent() {
    GetAliasesResponse response = client.admin().indices().prepareGetAliases('no_alias').get()
    int count = response.aliases.keys().size()
    assertThat(count, equalTo(0))
  }

  @Test
  void testAliasOfAlias() {
    String index = randIndex()
    String alias1 = randAlias()
    String alias2 = randAlias()
    client.admin().indices().prepareCreate(index).get()
    client.admin().indices().prepareAliases().addAlias(index, alias1).get()
    client.admin().indices().prepareAliases().addAlias(alias1, alias2).get()

    GetAliasesResponse response = client.admin().indices().prepareGetAliases(alias1).get()
    int count = response.aliases.keys().size()
    assertThat(count, equalTo(1))
    assertThat(response.aliases.containsKey(index), equalTo(true))

    response = client.admin().indices().prepareGetAliases(alias2).get()
    count = response.aliases.keys().size()
    assertThat(count, equalTo(1))
    assertThat(response.aliases.containsKey(index), equalTo(true))
  }

  private static String randIndex() {
    return 'index' + rand.nextLong()
  }

  private static String randAlias() {
    return 'alias' + rand.nextLong()
  }
}
