package com.compuality

import rx.Observable

class ExperimentReport {

  private DAO dao
  private Document doc

  ExperimentReport(DAO dao, Document doc) {
    this.dao = dao
    this.doc = doc
  }

  UUID getId() {
    return doc.id
  }

  Map<String, Object> getArguments() {
    return doc.arguments
  }

  Observable<GenerationReport> getGenerationReports() {
    return dao.getGenerationReports(doc)
  }

  static class Document {
    UUID id
    Map<String, Object> arguments
  }

  static class GenerationReport {

    private final DAO dao
    private final Document doc

    GenerationReport(DAO dao, Document doc) {
      this.dao = dao
      this.doc = doc
    }

    Observable<LifeReport> getLifeReports() {
      return dao.getLifeReports(doc)
    }

    static class Document {
      UUID experimentId
      long index
    }
  }

  static class LifeReport {

    private UUID experimentId
    private long generationIndex
    private double error

    double getError() { return error }
  }

  static interface DAO {

    Observable<ExperimentReport> getAll()

    ExperimentReport get(UUID id)

    Observable<GenerationReport> getGenerationReports(UUID experimentId)

    Observable<LifeReport> getLifeReports(UUID experimentId, long generationIndex)

    Observable<ExperimentReport> create(Observable<Map<String, Object>> arguments)
  }
}
