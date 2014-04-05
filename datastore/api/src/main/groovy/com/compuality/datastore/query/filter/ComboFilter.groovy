package com.compuality.datastore.query.filter

public interface ComboFilter extends Filter {

  List<Filter> getFilters()
}