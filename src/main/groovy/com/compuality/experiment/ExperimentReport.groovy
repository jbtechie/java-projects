package com.compuality.experiment

import rx.Observable

class ExperimentReport {

  UUID id
  Map<String, Object> args

  static class CreateRequest extends ExperimentReport {}

  static class GenerationReport {

    UUID experimentId
    long index

    static class CreateRequest extends GenerationReport {}
  }

  static class LifeReport {

    UUID experimentId
    long generationIndex
    double error

    static class CreateRequest extends LifeReport {}
  }

  static interface DAO {

    Observable<ExperimentReport> getAll()

    ExperimentReport get(UUID id)

    Observable<GenerationReport> getGenerationReports(UUID experimentId)

    Observable<LifeReport> getLifeReports(UUID experimentId, long generationIndex)

    Observable<ExperimentReport> createExperiments(Observable<CreateRequest> request)

    Observable<GenerationReport> createGenerations(Observable<GenerationReport.CreateRequest> request)

    Observable<LifeReport> createLives(Observable<LifeReport.CreateRequest> request)
  }
}
