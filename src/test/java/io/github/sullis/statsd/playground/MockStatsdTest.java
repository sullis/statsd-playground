package io.github.sullis.statsd.playground;

import me.kpavlov.mocks.statsd.server.MockStatsDServer;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class MockStatsdTest {
  @Test
  public void test() {
    MockStatsDServer server = new MockStatsDServer();
    server.start();
    assertThat(server.calls()).isEmpty();
    server.stop();
  }
}
