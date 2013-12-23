package com.compuality.elasticsearch

import com.google.inject.Inject

import rx.Observable

class DAOTest {

  @Inject
  DAOTest(ElasticSearchDAO dao) {
    dao.addObjects('experiments', 'exp', Observable.<Map>from([ [label:1], [label:2], [label:3]]))
  }
}
