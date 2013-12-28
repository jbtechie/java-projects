package com.compuality.experiment
import com.compuality.elasticsearch.ElasticSearchDAO
import com.compuality.experiment.ExperimentReport.CreateRequest
import com.compuality.experiment.ExperimentReport.GenerationReport
import com.compuality.experiment.ExperimentReport.LifeReport
import com.google.inject.Inject
import rx.Observable

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
  Observable<ExperimentReport> createExperiments(Observable<CreateRequest> requests) {
    dao.addObjects(INDEX, TYPE, requests)
  }

  @Override
  Observable<GenerationReport> createGenerations(Observable<GenerationReport.CreateRequest> request) {
    return null
  }

  @Override
  Observable<LifeReport> createLives(Observable<LifeReport.CreateRequest> request) {
    return null
  }
}
