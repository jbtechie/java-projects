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
  Observable<ExperimentReport> getAll() {
    return null
  }

  @Override
  ExperimentReport get(UUID id) {
    return null
  }

  @Override
  void loadExperiments(Observable<ExperimentReport> reports) {
    def prepared = reports.doOnEach({
      loadGenerations(it.generations)
    })

    esDao.addObjects('experiments','experiment', prepared).subscribe()
  }

  @Override
  void loadGenerations(Observable<GenerationReport> reports) {
    def prepared = reports.doOnEach({
      loadLives(it.lives)
    })

    esDao.addObjects('experiments','generation', prepared).subscribe()
  }

  @Override
  void loadLives(Observable<LifeReport> reports) {
    esDao.addObjects('experiments','life', reports).subscribe()
  }
}
