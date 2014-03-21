package com.compuality.store.document

import com.compuality.core.Identified
import com.compuality.core.Typed
import com.google.common.base.Optional
import org.joda.time.DateTime

import static com.google.common.base.Preconditions.checkNotNull

class StoredDocument implements Identified, Typed {

  private final String id
  private final String type
  private final String content

  private final Optional<DateTime> createdTime
  private final Optional<String> partition

  private StoredDocument(Builder builder) {
    this.id = checkNotNull(builder.id, 'id')
    this.type = checkNotNull(builder.type, 'type')
    this.content = checkNotNull(builder.content, 'content')
    this.createdTime = Optional.fromNullable(builder.createdTime)
    this.partition = Optional.fromNullable(builder.partition)
  }

  String getId() {
    return id
  }

  String getType() {
    return type
  }

  String getContent() {
    return content
  }

  Optional<DateTime> getCreatedTime() {
    return createdTime
  }

  Optional<String> getPartition() {
    return partition
  }

  static StoredDocument from(Document document) {
    return builder(document.id.get(), document.type, document.content)
          .createdTime(document.createdTime)
          .partition(document.partition)
          .build()
  }

  static Builder builder(String id, String type, String content) {
    return new Builder(id, type, content)
  }

  public static class Builder {

    private final String id
    private final String type
    private final String content
    private Optional<DateTime> createdTime
    private Optional<String> partition

    Builder(String id, String type, String content) {
      this.id = id
      this.type = type
      this.content = content
    }

    StoredDocument build() {
      return new StoredDocument(this)
    }

    Builder createdTime(DateTime createdTime) {
      this.createdTime = Optional.fromNullable(createdTime)
      return this
    }

    Builder createdTime(Optional<DateTime> createdTime) {
      this.createdTime = createdTime
      return this
    }

    Builder partition(String partition) {
      this.partition = Optional.fromNullable(partition)
      return this
    }

    Builder partition(Optional<String> partition) {
      this.partition = partition
      return this
    }
  }
}
