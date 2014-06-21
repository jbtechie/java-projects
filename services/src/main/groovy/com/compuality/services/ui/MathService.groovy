package com.compuality.services.ui
import com.compuality.inject.WebService

import javax.inject.Inject
import javax.ws.rs.Consumes
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path('ui/math')
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class MathService implements WebService {

  @Inject private Random rand

  @POST
  double post(int iterationCount) {
    println iterationCount
    List<Double> elems = []
    for(int i=0; i < iterationCount; ++i) {
       elems.add rand.nextFloat()
    }
    double sum = 0
    for(int i=0; i < iterationCount; ++i) {
      sum += elems[i]
    }
    println sum
    return sum
  }
}
