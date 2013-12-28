package com.compuality.experiment

import rx.Observable

class ExperimentReport {

  UUID id
  Map<String, Object> args
  Observable<GenerationReport> generations

  static class GenerationReport {

    UUID experimentId
    long index
    Observable<LifeReport> lives
  }

  static class LifeReport {

    UUID experimentId
    long generationIndex
    double error
  }

  static interface DAO {

    Observable<ExperimentReport> getAll()

    ExperimentReport get(UUID id)

    void loadExperiments(Observable<ExperimentReport> reports)
  }
}
