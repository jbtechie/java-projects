package com.compuality.elasticsearch

import com.google.inject.Inject

import rx.Observable

class DAOTest {

  @Inject
  DAOTest(ElasticSearchDAO dao) {
    dao.addObjectsWithHashId('experiments', 'exp', Observable.<Map>from([ [label:1], [label:2], [label:4]]))
  }
}
