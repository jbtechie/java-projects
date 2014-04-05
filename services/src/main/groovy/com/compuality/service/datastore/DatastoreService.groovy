package com.compuality.service.datastore
import com.compuality.datastore.Datastore
import com.compuality.datastore.IdentifiedObject
import com.compuality.inject.WebService
import com.google.common.collect.ImmutableList
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

import static com.compuality.datastore.query.filter.Filters.isClass

@Path('datastore')
@Produces(MediaType.APPLICATION_JSON)
class DatastoreService implements WebService {

  private static final Logger log = LoggerFactory.getLogger(DatastoreService)

  private final Datastore datastore
  private final Random rand

  @Inject
  DatastoreService(Datastore datastore, Random rand) {
    this.datastore = datastore
    this.rand = rand
  }

  @GET
  Iterable<IdentifiedObject<Result>> get() {
    List<IdentifiedObject<Result>> results = ImmutableList.copyOf(datastore.findTyped(isClass(Result.class)))
    results.each { log.info('Result val: {}', it.getObject().val) }
    return results
  }

  @GET
  @Path('store')
  Result store() {
    Result result = new Result(rand.nextInt())
    datastore.store([result])
    return result
  }

  static class Result {

    private final int val

    Result() {
    }

    Result(int val) {
      this.val = val
    }

    int getVal() {
      return val
    }
  }
}
