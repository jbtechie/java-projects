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
  float[] post(int iterationCount) {
    float[] elems = new float[iterationCount]
    for(int i=0; i < iterationCount; ++i) {
       elems[i] = rand.nextFloat()
    }
    return elems
  }
}
