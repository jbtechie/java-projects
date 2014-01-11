package com.compuality.experiment

import com.google.inject.AbstractModule
import com.google.inject.Scopes


class ExperimentModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(ElasticSearchReportDAO).in(Scopes.SINGLETON)

//    bind(ExperimentSandbox).asEagerSingleton()
  }
}
