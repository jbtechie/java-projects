import com.google.inject.Inject
import com.yammer.dropwizard.config.Environment
import org.eclipse.jetty.util.ajax.JSON

import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import java.util.concurrent.Callable
import java.util.concurrent.Executors

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
class GroovyResource {

  def rand

  @Inject
  GroovyResource(Environment env, Random rand) {
    env.addResource(this);
    this.rand = rand
  }

  @GET
  def groovy() {
    def f = { Thread.sleep(10); System.out.println('thread finished') }
    Executors.newFixedThreadPool(16).invokeAll((1..100).collectNested { f as Callable })
    System.out.println('done')
    return [rand: rand.nextInt()]
  }

  @GET
  @Path("map")
  def map() {
    def report =
      [
        arguments: [],
        metrics: [
          generations: [
            [time: 0.2, count: 55],
            [time: 0.4, count: 90],
            [time: 0.6, count: 1]
          ]
        ]
      ]

    def coords = [
      times: report.metrics.generations.collect { it.time },
      counts: report.metrics.generations.collect { it.count }
    ]

    return coords
  }

  @POST
  @Path("jsonTest")
  def jsonTest(String o) {
    if (!o)
      throw new WebApplicationException(Response.Status.BAD_REQUEST)
    return JSON.parse(o).foo
  }

  static class GroovyResponse {
    String message
  }
}
