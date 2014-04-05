package com.compuality.datastore.elasticsearch
import com.compuality.datastore.Datastore
import com.compuality.datastore.IdentifiedObject
import com.compuality.datastore.impl.IdentifiedObjectImpl
import com.compuality.datastore.query.filter.Filter
import com.compuality.datastore.query.filter.TypedFilter
import com.compuality.elasticsearch.client.ElasticSearchClientProvider
import com.compuality.interfaces.Identified
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.base.Function
import org.elasticsearch.action.bulk.BulkRequestBuilder
import org.elasticsearch.action.search.SearchRequestBuilder
import org.elasticsearch.action.search.SearchType
import org.elasticsearch.client.Client
import org.elasticsearch.index.query.FilterBuilder
import org.elasticsearch.index.query.QueryBuilder
import org.elasticsearch.search.SearchHit

import javax.inject.Inject

import static com.google.common.base.Preconditions.checkNotNull
import static org.elasticsearch.common.unit.TimeValue.timeValueHours
import static org.elasticsearch.index.query.QueryBuilders.filteredQuery
import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery

class ElasticSearchDatastore implements Datastore {

  private final ElasticSearchClientProvider clientProvider
  private final ElasticSearchDatastoreConfig config
  private final int scrollSize
  private final ObjectMapper mapper

  @Inject
  ElasticSearchDatastore(ElasticSearchClientProvider clientProvider, ObjectMapper mapper, ElasticSearchDatastoreConfig config) {
    this.clientProvider = checkNotNull(clientProvider, 'clientProvider')
    this.config = checkNotNull(config, 'config')
    this.mapper = mapper

    this.scrollSize = Math.max(1, (config.maxResultSize / config.shards).intValue())
  }

  @Override
  Iterable<IdentifiedObject<Object>> find(Filter query) {
    FilterBuilder filterBuilder = FilterToFilterBuilder.fromFilter(query)

    return null
  }

  @Override
  def <T> Iterable<IdentifiedObject<T>> findTyped(TypedFilter<T> query) {
    FilterBuilder filterBuilder = FilterToFilterBuilder.fromTypedFilter(query)
    return filter(filterBuilder).collect { hitsToObject().apply(it) }
  }

  @Override
  def <T> void store(List<T> objects) {
    BulkRequestBuilder bulkRequest = client().prepareBulk()
    objects.each {
      String doc = mapper.writeValueAsString(new ObjectDocument<>(it))
      bulkRequest.add(client().prepareIndex(config.index, it.class.name).setSource(doc))
    }
    bulkRequest.get()
  }

  @Override
  def <T> void update(List<IdentifiedObject<T>> identifiedObjects) {

  }

  @Override
  def <T> void delete(List<Identified> ids) {

  }

  private Client client() {
    return clientProvider.get()
  }

  private Iterable<SearchHit> filter(FilterBuilder filterBuilder) {
    QueryBuilder query = filteredQuery(matchAllQuery(), filterBuilder)

    SearchRequestBuilder searchRequest = clientProvider.get()
        .prepareSearch(config.index)
        .setSearchType(SearchType.SCAN)
        .setScroll(timeValueHours(1))
        .setSize(scrollSize)
        .setQuery(query)

    return new SearchHitIterable(client(), searchRequest)
  }

  private <T> Function<SearchHit, IdentifiedObject<T>> hitsToObject() {
    return HITS_TO_OBJECT
  }

  private final HitsToObject HITS_TO_OBJECT = new HitsToObject<?>()

  private class HitsToObject<T> implements Function<SearchHit, IdentifiedObject<T>> {

    @Override
    IdentifiedObject<T> apply(SearchHit hit) {
      String json = hit.getSourceAsString()
      ObjectDocument<T> doc = mapper.readValue(json, ObjectDocument.class)
      return new IdentifiedObjectImpl<>(hit.id, doc.getObject())
    }
  }
}
