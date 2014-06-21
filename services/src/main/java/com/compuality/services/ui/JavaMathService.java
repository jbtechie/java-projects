package com.compuality.services.ui;

import com.compuality.inject.WebService;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Random;

@Path("ui/math/java")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class JavaMathService implements WebService {

  private final Random rand;

  @Inject
  public JavaMathService(Random rand) {
    this.rand = rand;
  }

  @POST
  public float post(int iterationCount) {
    float sum = 0;
    for(int i=0; i < iterationCount; ++i) {
      sum += rand.nextFloat();
    }
    return sum;
  }
}
