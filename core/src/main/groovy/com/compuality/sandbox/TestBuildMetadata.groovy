package com.compuality.sandbox

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.google.common.io.Resources

import java.nio.charset.StandardCharsets

class TestBuildMetadata {

  static void main(String... args) {
    String metadata = Resources.toString(Resources.getResource('com/compuality/build-metadata.yml'), StandardCharsets.UTF_8)
    println 'Metadata: ' + metadata
    ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory())
    JsonNode root = yamlMapper.readTree(metadata)
    println 'Tree: '
    root.fields().each {
      println it.key + ' = ' + it.value
    }
    println 'Json: '
    ObjectMapper jsonMapper = new ObjectMapper()
    println jsonMapper.writeValueAsString(yamlMapper.readValue(metadata, new TypeReference<Map<String,String>>() {}))

    println 'Json pretty: '
    println jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(yamlMapper.readValue(metadata, Object))
  }
}