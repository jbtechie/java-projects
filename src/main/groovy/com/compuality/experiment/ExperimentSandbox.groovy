package com.compuality.experiment

import com.compuality.experiment.ExperimentReport.GenerationReport
import com.google.inject.Inject
import rx.Observable

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ExperimentSandbox {

  private static Logger logger = LoggerFactory.getLogger(ExperimentSandbox)

  private final ElasticSearchReportDAO dao

  @Inject
  ExperimentSandbox(ElasticSearchReportDAO dao) {
    this.dao = dao

    def id = UUID.randomUUID()
    def gen = Observable.from(new GenerationReport([experimentId:id, index:0]))
    def report = Observable.from(new ExperimentReport([id:id, args:[:], generations:gen]))

    dao.loadExperiments(report)
  }
}
