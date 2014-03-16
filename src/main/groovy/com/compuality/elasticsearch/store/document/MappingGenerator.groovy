package com.compuality.elasticsearch.store.document

import com.compuality.store.document.Document

public interface MappingGenerator {

  String map(Document document)
}