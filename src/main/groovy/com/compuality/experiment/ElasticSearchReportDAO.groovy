package com.compuality.experiment

import com.compuality.elasticsearch.ElasticSearchDAO
import com.compuality.experiment.ExperimentReport.DAO
import com.compuality.experiment.ExperimentReport.GenerationReport
import com.compuality.experiment.ExperimentReport.LifeReport
import com.google.inject.Inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import rx.Observable

class ElasticSearchReportDAO implements DAO {

  private static final Logger logger = LoggerFactory.getLogger(ElasticSearchReportDAO)

  private final ElasticSearchDAO esDao

  @Inject
  ElasticSearchReportDAO(ElasticSearchDAO esDao) {
    this.esDao = esDao
  }

  @Override
  rx.Observable<ExperimentReport> getAll() {
    return null
  }

  @Override
  ExperimentReport get(UUID id) {
    return null
  }

  @Override
  void loadExperiments(Observable<ExperimentReport> reports) {
    def split = reports.publish()
    esDao.addObjects('experiments','experiment', split).subscribe()
    split.doOnEach({
      logger.debug('loading gens on each exp')
      loadGenerations(it.generations)
    }).subscribe()
    split.connect()
  }

  @Override
  void loadGenerations(Observable<GenerationReport> reports) {
    def split = reports.publish()
    esDao.addObjects('experiments','generation', split).subscribe()
    split.doOnEach({
      loadLives(it.lives)
    }).subscribe()
    split.connect()
  }

  @Override
  void loadLives(Observable<LifeReport> reports) {
    esDao.addObjects('experiments','life', reports).subscribe()
  }
}
