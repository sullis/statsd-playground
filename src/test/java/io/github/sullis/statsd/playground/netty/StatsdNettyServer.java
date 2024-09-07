package io.github.sullis.statsd.playground.netty;

import io.netty.channel.unix.DomainSocketAddress;
import io.netty.handler.logging.LogLevel;
import java.io.File;
import java.net.InetSocketAddress;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import reactor.core.publisher.Flux;
import reactor.netty.udp.UdpServer;

public class StatsdNettyServer {
  private final String UDS_DATAGRAM_SOCKET_PATH = "/tmp/StatsdNettyServer-" + UUID.randomUUID() + ".sock";
  private final AtomicInteger serverMetricReadCount = new AtomicInteger();
  private boolean bound = false;
  private UdpServer udpServer;

  public StatsdNettyServer(final StatsdProtocol protocol, final int port) {
    udpServer = UdpServer.create()
        .bindAddress(() -> protocol == StatsdProtocol.UDP
            ? InetSocketAddress.createUnresolved("localhost", port) : newDomainSocketAddress(UDS_DATAGRAM_SOCKET_PATH))
        .handle((in, out) -> in.receive()
            .asString()
            .flatMap(packet -> Flux.just(packet.split("\n")))
            .flatMap(packetLine -> {
              serverMetricReadCount.getAndIncrement();
              return Flux.never();
            }))
        .doOnBound((server) -> bound = true)
        .doOnUnbound((server) -> bound = false)
        .wiretap("udpserver", LogLevel.INFO);
  }

  public boolean isBound() {
    return bound;
  }

  public void close() {
    if (udpServer != null) {
      // close resources?
    }
  }

  private static DomainSocketAddress newDomainSocketAddress(String path) {
    try {
      File tempFile = new File(path);
      tempFile.delete();
      tempFile.deleteOnExit();
      return new DomainSocketAddress(tempFile);
    }
    catch (Exception e) {
      throw new RuntimeException("Error creating a temporary file", e);
    }
  }
}
