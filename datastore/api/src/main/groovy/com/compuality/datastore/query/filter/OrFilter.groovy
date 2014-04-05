package com.compuality.datastore.query.filter

import com.google.common.collect.Lists

class OrFilter implements ComboFilter {

  private final List<Filter> filters

  OrFilter(Filter... filters) {
    this.filters = Lists.newArrayList(filters)
  }

  OrFilter or(Filter... filters) {
    this.filters.addAll(filters)
    return this
  }

  List<Filter> getFilters() {
    return filters
  }
}
