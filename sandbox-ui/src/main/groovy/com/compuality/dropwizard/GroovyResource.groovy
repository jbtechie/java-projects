package com.compuality.dropwizard

import com.compuality.cpu.CPU
import com.fasterxml.jackson.databind.ObjectMapper
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
  def mapper = new ObjectMapper()

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
  @Path("entries")
  def entries() {
    def l = [1,2,3]
    return l.collectEntries { [it, it**2] }
  }

  @GET
  @Path("map")
  def map() {
    def report =
      [
        arguments: [:],
        metrics: [
          generations: [
          ]
        ]
      ]

    (1..100).each { i ->
      new CPU(6).with { cpu ->
        randMem()
        cpu.sim(cpu.WORD_SIZE)
        cpu.mem.eachWithIndex { it, idx ->
          report.metrics.generations.add([x:idx, y:it, z:1])
        }
      }
    }

    def hist = [:]
    report.metrics.generations.each {
      def key = [it.x, ((int)it.y/100)*100]
      if(key in hist)
        hist[key] += it.z
      else
        hist[key] = it.z
    }

    def coords = [x:[], y:[], z:[]]
    hist.each { k, v ->
      coords.x << k[0]
      coords.y << k[1]
      coords.z << v
    }

    println coords.x.size()

    return coords
  }

  @POST
  @Path("jsonTest")
  def jsonTest(String o) {
    if (!o)
      throw new WebApplicationException(Response.Status.BAD_REQUEST)
    return JSON.parse(o).foo
  }

  @GET
  @Path("run")
  def run() {
//    def cpus = (1..2).collect { new com.compuality.cpu.CPU(6)}
//    return cpus

    def cpu = new CPU(6).with {
      randMem()
      it
    }

    def before = JSON.parse(mapper.writeValueAsString(cpu))
    cpu.sim(2**13)
    def after = JSON.parse(mapper.writeValueAsString(cpu))

//    return before == after
    return [before:before.mem, after:after.mem]

//    def json = new ObjectMapper().writeValueAsString(expected)
//    def test = new com.compuality.cpu.CPU(JSON.parse(json))
//    return json.length()
  }

  @GET
  @Path('sleep')
  def sleep() {
    Thread.sleep(62*1000)
  }

  static class GroovyResponse {
    String message
  }
}
