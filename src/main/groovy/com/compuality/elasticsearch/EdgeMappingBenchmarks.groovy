package com.compuality.elasticsearch
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.collect.ImmutableMap
import com.google.common.io.Resources
import com.yammer.metrics.core.Clock
import org.elasticsearch.ElasticsearchException
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse
import org.elasticsearch.action.bulk.BulkRequestBuilder
import org.elasticsearch.action.bulk.BulkResponse
import org.elasticsearch.action.search.SearchRequestBuilder
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.client.Client
import org.elasticsearch.index.query.FilterBuilder
import org.elasticsearch.index.query.FilterBuilders
import org.elasticsearch.index.query.QueryBuilder
import org.elasticsearch.index.query.QueryBuilders
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.inject.Inject
import java.nio.charset.Charset
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicLong

public class EdgeMappingBenchmarks {

  private static final Logger log = LoggerFactory.getLogger(EdgeMappingBenchmarks)

  private static final String EDGE_TYPE = 'edge'
  private static final int MAX_ID = 10000
  private static final int MAX_LABEL = 10

  private static class Indices {
    static final String DOC_PER_EDGE_WITH_SOURCE = 'doc_per_edge_with_source'
    static final String DOC_PER_EDGE_NO_SOURCE = 'doc_per_edge_no_source'
    static final String DOC_PER_EDGE_NO_SOURCE_NO_ALL = 'doc_per_edge_no_source_no_all'
  }

  private final Client client
  private final Charset charset
  private final ObjectMapper mapper

  @Inject
  EdgeMappingBenchmarks(Client client, Charset charset, ObjectMapper mapper) {
    this.client = client
    this.charset = charset
    this.mapper = mapper

    Map<String, String> mappings = ImmutableMap.of(
        Indices.DOC_PER_EDGE_WITH_SOURCE,      getMapping(Indices.DOC_PER_EDGE_WITH_SOURCE),
        Indices.DOC_PER_EDGE_NO_SOURCE,        getMapping(Indices.DOC_PER_EDGE_NO_SOURCE),
        Indices.DOC_PER_EDGE_NO_SOURCE_NO_ALL, getMapping(Indices.DOC_PER_EDGE_NO_SOURCE_NO_ALL))

    mappings.each { index, mapping -> benchmarkLoad(index, mapping, 100, 10000) }

//    mappings.keySet().each { index -> benchmarkQuery(index, 100000, !index.equals(Indices.DOC_PER_EDGE_WITH_SOURCE)) }
  }

  private void benchmarkLoad(final String index, final String mapping, final long batches, final long batchSize) {

    if(client.admin().indices().prepareExists(index).get().exists) {
      client.admin().indices().prepareDelete(index).get()
    }
    client.admin().indices().prepareCreate(index).get()

    PutMappingResponse putMappingResponse = client.admin().indices()
        .preparePutMapping(index)
        .setType(EDGE_TYPE)
        .setSource(mapping)
        .get()

    if(!putMappingResponse.acknowledged) {
      log.error('Put mapping of type "{}" in index "{}" not acknowledged.', EDGE_TYPE, index)
    }

    ExecutorService executor = Executors.newFixedThreadPool(2)

    Clock clock = Clock.defaultClock()
    final long startTime = clock.tick()

    List<Callable> callables = (1..batches).collect {
      return {
        ThreadLocalRandom rand = ThreadLocalRandom.current()
      BulkRequestBuilder bulkRequest = client.prepareBulk()

      (1..batchSize).each {
        Edge edge = new Edge(rand.nextInt(MAX_ID).toString(), rand.nextInt(MAX_ID).toString(), rand.nextInt(MAX_LABEL).toString())
        bulkRequest.add(client.prepareIndex(index, EDGE_TYPE).setSource(mapper.writeValueAsString(edge)))
      }

      BulkResponse bulkResponse = bulkRequest.get()

      if(bulkResponse.hasFailures()) {
        log.error('Bulk request failed: {}', bulkResponse.buildFailureMessage())
      }
      }
    }

    executor.invokeAll(callables)
    executor.shutdown()
    executor.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS)

    final double duration = (clock.tick() - startTime) / 1e9
    final long totalEdges = batches * batchSize
    final double rate = totalEdges / duration

    log.info('Loaded index "{}" with {} edges in {} seconds ({} e/s).', index, totalEdges, duration, rate)
  }

  private void benchmarkQuery(final String index, final long queries, final boolean bFields) {

    Clock clock = Clock.defaultClock()
    final long startTime = clock.tick()

    AtomicLong totalHits = new AtomicLong()

    ExecutorService executor = Executors.newFixedThreadPool(4)

    List<Callable> callables = (1..queries).collect {
      return {
      ThreadLocalRandom rand = ThreadLocalRandom.current()
      QueryBuilder matchAll = QueryBuilders.matchAllQuery()
      FilterBuilder filter = FilterBuilders.termFilter('source', rand.nextInt(MAX_ID).toString())
      QueryBuilder query = QueryBuilders.filteredQuery(matchAll, filter)

      SearchRequestBuilder searchRequest = client.prepareSearch(index)
          .setTypes(EDGE_TYPE)
//          .setSearchType(SearchType.SCAN)
          .setQuery(query)

      if(bFields) {
        searchRequest.addField('*')
      }

      try {
        SearchResponse response = searchRequest.get()

        if(response.failedShards > 0) {
          log.error('Query failed on {} shards.', response.failedShards)
        }

        totalHits.addAndGet(response.hits.hits.size())
      } catch(ElasticsearchException e) {
//        log.error('Failed performing query: {}', searchRequest)
        log.error('Detailed message: {}', e.detailedMessage)
      }
      }
    }

    executor.invokeAll(callables)
    executor.shutdown()
    executor.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS)

    final double duration = (clock.tick() - startTime) / 1e9
    final double queryRate = queries / duration
    final double hitRate = totalHits.get() / duration

    log.info('Performed {} queries on index "{}" in {} seconds ({} q/s) resulting in {} hits ({} h/s).', queries, index, duration, queryRate, totalHits, hitRate)
  }

  private String getMapping(String index) {
    URL mappingURL = Resources.getResource(EdgeMappingBenchmarks, 'mappings/' + index + '.json')
    return Resources.toString(mappingURL, charset)
  }

  private static class Edge {
    String source
    String destination
    String label

    Edge(String source, String destination, String label) {
      this.source = source
      this.destination = destination
      this.label = label
    }
  }
}
