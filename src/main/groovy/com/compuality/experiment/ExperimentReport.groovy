package com.compuality.experiment

import com.compuality.rx.Observables
import com.fasterxml.jackson.annotation.JsonIgnore
import rx.Observable

class ExperimentReport {

  UUID id
  Map<String, Object> args

  @JsonIgnore
  Observable<GenerationReport> generations = Observables.complete

  static class GenerationReport {

    UUID experimentId
    long index
    double totalError

    @JsonIgnore
    Observable<LifeReport> lives = Observables.complete
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

    void loadGenerations(Observable<GenerationReport> reports)

    void loadLives(Observable<LifeReport> reports)
  }
}
