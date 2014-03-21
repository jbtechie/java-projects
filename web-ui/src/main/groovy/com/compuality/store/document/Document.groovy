package com.compuality.store.document

import com.compuality.core.Typed
import com.google.common.base.Optional
import org.joda.time.DateTime

import static com.google.common.base.Preconditions.checkNotNull

class Document implements Typed {

  private final String type
  private final String content

  private final Optional<String> id
  private final Optional<DateTime> createdTime
  private final Optional<String> partition

  private Document(Builder builder) {
    this.type = checkNotNull(builder.type, 'type')
    this.content = checkNotNull(builder.content, 'content')
    this.id = Optional.fromNullable(builder.id)
    this.createdTime = Optional.fromNullable(builder.createdTime)
    this.partition = Optional.fromNullable(builder.partition)
  }

  String getType() {
    return type
  }

  String getContent() {
    return content
  }

  Optional<String> getId() {
    return id
  }

  Optional<DateTime> getCreatedTime() {
    return createdTime
  }

  Optional<String> getPartition() {
    return partition
  }

  public static class Builder {

    private final String type
    private final String content
    private String id
    private DateTime createdTime
    private String partition

    Builder(String type, String content) {
      this.type = type
      this.content = content
    }

    Document build() {
      return new Document(this)
    }

    Builder id(String id) {
      this.id = id
      return this
    }

    Builder createdTime(DateTime createdTime) {
      this.createdTime = createdTime
      return this
    }

    Builder partition(String partition) {
      this.partition = partition
      return this
    }
  }
}
