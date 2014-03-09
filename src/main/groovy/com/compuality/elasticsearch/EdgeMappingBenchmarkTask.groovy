package com.compuality.elasticsearch

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.collect.ImmutableMap
import com.google.common.collect.ImmutableMultimap
import com.google.common.io.Resources
import com.yammer.dropwizard.config.Environment
import com.yammer.dropwizard.tasks.Task
import com.yammer.metrics.core.Clock
import org.elasticsearch.ElasticsearchException
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse
import org.elasticsearch.action.bulk.BulkRequestBuilder
import org.elasticsearch.action.bulk.BulkResponse
import org.elasticsearch.action.search.SearchRequestBuilder
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.action.search.SearchType
import org.elasticsearch.client.Client
import org.elasticsearch.common.unit.TimeValue
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

public class EdgeMappingBenchmarkTask extends Task {

  public static final String TASK_NAME = 'benchmark-edge-mapping'

  private static final Logger log = LoggerFactory.getLogger(EdgeMappingBenchmarkTask)

  private static final String EDGE_TYPE = 'edge'
  private static final int MAX_ID = 100
  private static final int MAX_LABEL = 10

  private static class Indices {
    static final String DOC_PER_EDGE_WITH_SOURCE = 'doc_per_edge_with_source'
    static final String DOC_PER_EDGE_NO_SOURCE = 'doc_per_edge_no_source'
    static final String DOC_PER_EDGE_NO_SOURCE_NO_ALL = 'doc_per_edge_no_source_no_all'
  }

  private static enum Phase {
    Load,
    Query,
    Clean

    private static final Map<String, Phase> stringMap = values().collectEntries { [it.name().toLowerCase(), it] }

    public static Phase fromString(String string) {
      String comparable = string.toLowerCase()
      Phase phase = stringMap[comparable]
      if(phase) {
        return phase
      } else {
        throw new IllegalArgumentException("Could not create " + Phase + " from String " + string)
      }
    }
  }

  private final Client client
  private final Charset charset
  private final ObjectMapper mapper
  private final Map<String, String> mappings


  @Inject
  EdgeMappingBenchmarkTask(Environment env, Client client, Charset charset, ObjectMapper mapper) {
    super(TASK_NAME)

    this.client = client
    this.charset = charset
    this.mapper = mapper

    mappings = ImmutableMap.of(
        Indices.DOC_PER_EDGE_WITH_SOURCE, getMapping(Indices.DOC_PER_EDGE_WITH_SOURCE),
        Indices.DOC_PER_EDGE_NO_SOURCE, getMapping(Indices.DOC_PER_EDGE_NO_SOURCE),
        Indices.DOC_PER_EDGE_NO_SOURCE_NO_ALL, getMapping(Indices.DOC_PER_EDGE_NO_SOURCE_NO_ALL))

    env.addTask(this)
  }

  private static class Report {
    Options options
    Results results
  }

  private static class Options {
    Set<Phase> phases
    Long batches
    Long batchSize
    Long queries
    Integer threads
  }

  private static class Results {
    List load
    List query
  }

  private static class LoadResults {
    String index
    Long totalEdges
    Double duration
    Double rate
  }

  private static class QueryResults {
    String index
    Double duration
    Double queryRate
    Long totalHits
    Long hitsProcessed
    Double hitProcessRate
  }

  @Override
  void execute(ImmutableMultimap<String, String> parameters, PrintWriter output) throws Exception {

    Options options = new Options()
    options.phases = parameters.get('phase').collect { Phase.fromString(it) } as Set

    if(!options.phases) {
      output.println('No phases specified. Supported phases include: ' + Phase.values())
    }

    if(Phase.Load in options.phases) {
      options.batches = parameters.get('batches').iterator().next() as long
      options.batchSize = parameters.get('batch_size').iterator().next() as long
    }

    if(Phase.Query in options.phases) {
      options.queries = parameters.get('queries').iterator().next() as long
    }

    Results results

    if(Phase.Load in options.phases || Phase.Query in options.phases) {
      results = new Results()
      options.threads = parameters.get('threads').iterator().next() as long
    }

    if(Phase.Load in options.phases) {
      results.load = []
      mappings.each { index, mapping -> results.load << benchmarkLoad(index, mapping, options) }
    }

    if(Phase.Query in options.phases) {
      results.query = []
      mappings.keySet().each { index -> results.query << benchmarkQuery(index, options, !index.equals(Indices.DOC_PER_EDGE_WITH_SOURCE)) }
    }

    if(Phase.Clean in options.phases) {
      mappings.keySet().each { index -> deleteIndexIfExists(index) }
    }

    Report report = new Report(options:options, results:results)
    String reportJson = mapper.writeValueAsString(report)
    output.println(reportJson)
    client.prepareIndex('experiments', TASK_NAME).setSource(reportJson).get()
  }

  private void deleteIndexIfExists(String index) {
    if(client.admin().indices().prepareExists(index).get().exists) {
      client.admin().indices().prepareDelete(index).get()
    }
  }

  private LoadResults benchmarkLoad(final String index, final String mapping, Options options) {

    deleteIndexIfExists(index)
    client.admin().indices().prepareCreate(index).get()

    PutMappingResponse putMappingResponse = client.admin().indices()
        .preparePutMapping(index)
        .setType(EDGE_TYPE)
        .setSource(mapping)
        .get()

    if(!putMappingResponse.acknowledged) {
      log.error('Put mapping of type "{}" in index "{}" not acknowledged.', EDGE_TYPE, index)
    }

    ExecutorService executor = Executors.newFixedThreadPool(options.threads)

    Clock clock = Clock.defaultClock()
    final long startTime = clock.tick()

    List<Callable> callables = (1..options.batches).collect {
      return {
        ThreadLocalRandom rand = ThreadLocalRandom.current()
      BulkRequestBuilder bulkRequest = client.prepareBulk()

      (1..options.batchSize).each {
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

    LoadResults results = new LoadResults()

    results.duration = (clock.tick() - startTime) / 1e9
    results.totalEdges = options.batches * options.batchSize
    results.rate = results.totalEdges / results.duration

    return results
  }

  private QueryResults benchmarkQuery(final String index, Options options, final boolean bFields) {

    Clock clock = Clock.defaultClock()
    final long startTime = clock.tick()

    AtomicLong totalHits = new AtomicLong()
    AtomicLong hitsProcessed = new AtomicLong()

    ExecutorService executor = Executors.newFixedThreadPool(options.threads)

    List<Callable> callables = (1..options.queries).collect {
      return {
      ThreadLocalRandom rand = ThreadLocalRandom.current()
      QueryBuilder matchAll = QueryBuilders.matchAllQuery()
      FilterBuilder filter = FilterBuilders.termFilter('source', rand.nextInt(MAX_ID).toString())
      QueryBuilder query = QueryBuilders.filteredQuery(matchAll, filter)

      SearchRequestBuilder searchRequest = client.prepareSearch(index)
          .setTypes(EDGE_TYPE)
          .setSearchType(SearchType.SCAN)
          .setScroll(TimeValue.timeValueSeconds(1))
          .setQuery(query)

      if(bFields) {
        searchRequest.addField('*')
      }

      try {
        SearchResponse response = searchRequest.get()

        if(response.failedShards > 0) {
          log.error('Query failed on {} shards.', response.failedShards)
        }

        totalHits.addAndGet(response.hits.totalHits)
        hitsProcessed.addAndGet(response.hits.hits.size())

      } catch(ElasticsearchException e) {
//        log.error('Failed performing query: {}', searchRequest)
        log.error('Detailed message: {}', e.detailedMessage)
      }
      }
    }

    executor.invokeAll(callables)
    executor.shutdown()
    executor.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS)

    QueryResults results = new QueryResults()

    results.duration = (clock.tick() - startTime) / 1e9
    results.queryRate = options.queries / results.duration
    results.totalHits = totalHits.get()
    results.hitsProcessed = hitsProcessed.get()
    results.hitProcessRate = results.hitsProcessed / results.duration

    return results
  }

  private String getMapping(String index) {
    URL mappingURL = Resources.getResource(EdgeMappingBenchmarkTask, 'mappings/' + index + '.json')
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
