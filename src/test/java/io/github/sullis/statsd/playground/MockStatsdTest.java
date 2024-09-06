package io.github.sullis.statsd.playground;

import com.timgroup.statsd.NonBlockingStatsDClientBuilder;
import com.timgroup.statsd.StatsDClient;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import me.kpavlov.mocks.statsd.server.MockStatsDServer;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class MockStatsdTest {
  @Test
  public void test() {
    final String prefix = "prefix" + System.currentTimeMillis();

    MockStatsDServer server = new MockStatsDServer();
    server.start();
    assertThat(server.calls()).isEmpty();

    StatsDClient client = new NonBlockingStatsDClientBuilder()
        .prefix(prefix)
        .aggregationFlushInterval(0)
        .hostname("localhost")
        .port(server.port())
        .build();

    final String counterName = "counter" + UUID.randomUUID();

    client.incrementCounter(counterName);

    Awaitility.await()
        .atMost(Duration.ofSeconds(5))
        .until(() -> {
          List<String> calls = server.calls();
          assertThat(calls)
              .hasSize(1);
          assertThat(calls.get(0))
              .isEqualTo(prefix + "." + counterName  + ":1|c\n");
          return true;
    });

    client.incrementCounter(counterName);
    Awaitility.await()
        .atMost(Duration.ofSeconds(5))
        .until(() -> {
          List<String> calls = server.calls();
          assertThat(calls)
              .hasSize(2);
          assertThat(calls.get(1))
              .isEqualTo(prefix + "." + counterName  + ":1|c\n");
          assertThat(calls.get(0))
              .isEqualTo(prefix + "." + counterName  + ":1|c\n");
          return true;
        });

    server.stop();
    client.stop();
    client.close();
  }
}
