package com.compuality.experiment
import com.google.inject.Inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ExperimentSandbox {

  private static Logger logger = LoggerFactory.getLogger(ExperimentSandbox)

  private final ElasticSearchReportDAO dao

  @Inject
  ExperimentSandbox(ElasticSearchReportDAO dao) {
    this.dao = dao

//    dao.loadExperiments(Observable.from(new ExperimentReport([id:UUID.randomUUID(), args:[:]])))
  }
}
