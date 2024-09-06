package io.github.sullis.statsd.playground;

import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.Ports;
import java.util.ArrayList;
import java.util.List;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;


public class GraphiteContainer extends GenericContainer {
  public static final int STATSD_UDP_PORT = 8125;

  public GraphiteContainer() {
    this(DockerImageName.parse("graphiteapp/graphite-statsd"));
  }

  public GraphiteContainer(DockerImageName imageName) {
    super(imageName);

    withCreateContainerCmdModifier(cmd -> {
      CreateContainerCmd createContainerCmd = (CreateContainerCmd) cmd;
      // Add previously exposed ports and UDP port
      List<ExposedPort> exposedPorts = new ArrayList<>();
      for (ExposedPort p : createContainerCmd.getExposedPorts()) {
        exposedPorts.add(p);
      }
      exposedPorts.add(ExposedPort.udp(STATSD_UDP_PORT));
      createContainerCmd.withExposedPorts(exposedPorts);

      // Add previous port bindings and UDP port binding
      Ports ports = createContainerCmd.getPortBindings();
      ports.bind(ExposedPort.udp(STATSD_UDP_PORT), Ports.Binding.empty());
      createContainerCmd.withPortBindings(ports);
    });
  }
}
