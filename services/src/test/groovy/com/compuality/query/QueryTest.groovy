package com.compuality.query

import org.junit.Test

import static CoreConstraints.equalTo
import static CoreConstraints.greaterThan

class QueryTest {

  static class Example {

    int id
    InetAddress address = new InetAddress()

    static class InetAddress {
      String address
      int port
    }
  }

  @Test
  void test() {
    Example entity = Query.pathGenerator(Example)

    Query.on(entity)
        .where(entity.id, equalTo(3))
        .and(entity.address.port, greaterThan(1024))
  }
}
