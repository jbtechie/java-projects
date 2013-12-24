package com.compuality.experiment

import com.compuality.elasticsearch.ElasticSearchDAO
import com.compuality.experiment.ExperimentReport.CreateRequest
import com.compuality.experiment.ExperimentReport.GenerationReport
import com.compuality.experiment.ExperimentReport.LifeReport
import com.google.inject.Inject
import rx.Observable
import rx.Observable.OnSubscribeFunc

class ElasticSearchExperimentReportDAO implements ExperimentReport.DAO {

  private static final String INDEX = 'experiments'
  private static final String TYPE = 'experiment'

  private ElasticSearchDAO dao

  @Inject
  ElasticSearchExperimentReportDAO(ElasticSearchDAO dao) {
    this.dao = dao
  }

  @Override
  Observable<ExperimentReport> getAll() {
    return null
  }

  @Override
  ExperimentReport get(UUID id) {
    return null
  }

  @Override
  Observable<ExperimentReport.GenerationReport> getGenerationReports(UUID experimentId) {
    return null
  }

  @Override
  Observable<ExperimentReport.LifeReport> getLifeReports(UUID experimentId, long generationIndex) {
    return null
  }

  @Override
  Observable<ExperimentReport> createExperiment(Observable<CreateRequest> request) {
    return Observable.create({ observer ->

      dao.addObjects(INDEX, TYPE, request)

    } as OnSubscribeFunc)
  }

  @Override
  Observable<GenerationReport> createGeneration(Observable<GenerationReport.CreateRequest> request) {
    return null
  }

  @Override
  Observable<LifeReport> createLife(Observable<LifeReport.CreateRequest> request) {
    return null
  }
}
