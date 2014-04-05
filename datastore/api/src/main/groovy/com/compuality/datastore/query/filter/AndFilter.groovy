package com.compuality.datastore.query.filter

import com.google.common.collect.Lists

class AndFilter implements ComboFilter {

  private final List<Filter> filters

  AndFilter(Filter... filters) {
    this.filters = Lists.newArrayList(filters)
  }

  AndFilter and(Filter... filters) {
    this.filters.addAll(filters)
    return this
  }

  List<Filter> getFilters() {
    return filters
  }
}
