package io.github.sullis.statsd.playground;

import com.timgroup.statsd.NonBlockingStatsDClientBuilder;
import com.timgroup.statsd.StatsDClient;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import me.kpavlov.mocks.statsd.server.MockStatsDServer;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;


@Testcontainers
public class TestContainersTest {
  @Container
  private static final GraphiteContainer graphite = new GraphiteContainer();

  @Test
  public void test() {
    assertThat(graphite.isRunning()).isTrue();
    final String prefix = "prefix" + System.currentTimeMillis();

    StatsDClient client = new NonBlockingStatsDClientBuilder()
        .prefix(prefix)
        .aggregationFlushInterval(0)
        .hostname("localhost")
        .port(GraphiteContainer.STATSD_UDP_PORT)
        .build();

    final String counterName = "counter" + UUID.randomUUID();

    client.incrementCounter(counterName);
    client.incrementCounter(counterName);

    client.stop();
    client.close();
  }
}
