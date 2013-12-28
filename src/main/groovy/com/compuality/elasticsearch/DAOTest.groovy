package com.compuality.elasticsearch

import com.google.inject.Inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import rx.Observable

class DAOTest {

  private static Logger logger = LoggerFactory.getLogger(DAOTest)

  @Inject
  DAOTest(ElasticSearchDAO dao) {
    dao.addObjectsWithHashId('experiments', 'exp',
      Observable.<Map>from([ [label:1], [label:2], [label:4]]))
        .subscribe({ logger.debug("add response: " + it.toString()) })
  }
}
