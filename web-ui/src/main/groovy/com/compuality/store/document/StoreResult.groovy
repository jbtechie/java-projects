package com.compuality.store.document
import com.google.common.base.Optional

class StoreResult {

  private final boolean allSuccessful
  private final List<ResultItem> resultItems

  StoreResult(boolean allSuccessful, List<ResultItem> resultItems) {
    this.allSuccessful = allSuccessful
    this.resultItems = resultItems
  }

  boolean isAllSuccessful() {
    return allSuccessful
  }

  List<ResultItem> getResultItems() {
    return resultItems
  }

  Iterable<StoredDocument> getSuccessful() {
    return resultItems.findAll { !it.failureMessage.present }.collect { StoredDocument.from(it.document) }
  }

  Iterable<Document> getFailed() {
    return resultItems.findAll { it.failureMessage.present }.collect { it.document }
  }

  static class ResultItem {

    private final Document document
    private final Optional<String> failureMessage

    ResultItem(Document document, Optional<String> failureMessage) {
      this.document = document
      this.failureMessage = failureMessage
    }

    Document getDocument() {
      return document
    }

    Optional<String> getFailureMessage() {
      return failureMessage
    }
  }
}
