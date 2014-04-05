package com.compuality.datastore.elasticsearch

import com.google.common.collect.AbstractIterator
import org.elasticsearch.action.search.SearchRequestBuilder
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.client.Client
import org.elasticsearch.common.unit.TimeValue
import org.elasticsearch.search.SearchHit
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static com.google.common.base.Preconditions.checkNotNull

class SearchHitIterable implements Iterable<SearchHit> {

  private static final Logger log = LoggerFactory.getLogger(SearchHitIterable)

  private final Client client
  private final SearchRequestBuilder request

  SearchHitIterable(Client client, SearchRequestBuilder request) {
    this.client = checkNotNull(client, 'client')
    this.request = checkNotNull(request, 'request')
  }

  @Override
  Iterator<SearchHit> iterator() {
    return new SearchIterator(client, request.get())
  }

  static class SearchIterator extends AbstractIterator<SearchHit> {

    private static final Logger log = LoggerFactory.getLogger(SearchIterator)

    private final Client client
    private SearchResponse response
    private Iterator<SearchHit> hitsIterator

    SearchIterator(Client client, SearchResponse response) {
      this.client = checkNotNull(client, 'client')
      this.response = response
      this.hitsIterator = response.hits.iterator()
    }

    @Override
    protected SearchHit computeNext() {
      if(!hitsIterator.hasNext() && response.scrollId) {
        response = client.prepareSearchScroll(response.scrollId).get(TimeValue.timeValueHours(1))
        hitsIterator = response.hits.iterator()
      }

      if(hitsIterator.hasNext()) {
        return hitsIterator.next()
      }

      return endOfData()
    }
  }
}
