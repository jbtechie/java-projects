package com.compuality.experiment

import com.fasterxml.jackson.annotation.JsonIgnore
import rx.Observable

class ExperimentReport {

  UUID id
  Map<String, Object> args

  @JsonIgnore
  Observable<GenerationReport> generations = Observable.empty()

  static class GenerationReport {

    long index

    Map<Object, Class> edges

    double totalError

    @JsonIgnore
    Observable<LifeReport> lives = Observable.empty()
  }

  static class LifeReport {

    UUID id

    Map<Object, Class> edges

    Set<Object> keys
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
