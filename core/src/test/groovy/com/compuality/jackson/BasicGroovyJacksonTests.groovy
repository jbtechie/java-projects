package com.compuality.jackson
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.google.common.collect.Lists
import org.junit.Test

class BasicGroovyJacksonTests {

  def mapper = new ObjectMapper(new YAMLFactory())

  @Test
  void serializeTest() {
    def blockStyleWithSingleQuotes = "{'asdf':1234,'lkj':987}"
    def node = mapper.readTree(blockStyleWithSingleQuotes)
    println "node (${blockStyleWithSingleQuotes.length()}): ${node}"
    def serializedNode = "${node}"
    println 'serialized: ' + serializedNode
    def inlineYaml = 'asdf: 1234\nlkj: 987'
    println inlineYaml
//    def inlineYamlFromResource =  Resources.toString(Resources.getResource('jackson/multiline.yaml'), StandardCharsets.UTF_8)
//    println inlineYamlFromResource
    node = mapper.readTree(inlineYaml)
    println "node (${inlineYaml.length()}): ${node}"
    def k = 'asdf'
    print "${k}: ${node.get(k)}"
    println()
    node.fields().next().with { print "${key}: ${value}"}
    println()
    Lists.newArrayList(node.fields()).each {  println "${it.key}: ${it.value}" }
    println mapper.writeValueAsString([asdf:1234,lkj:987])
  }
}
