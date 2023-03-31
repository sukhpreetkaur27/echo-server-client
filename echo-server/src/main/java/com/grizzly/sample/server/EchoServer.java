package com.grizzly.sample.server;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Optional;
import java.util.logging.Logger;

import org.glassfish.grizzly.filterchain.FilterChainBuilder;
import org.glassfish.grizzly.filterchain.TransportFilter;
import org.glassfish.grizzly.nio.transport.TCPNIOTransport;
import org.glassfish.grizzly.nio.transport.TCPNIOTransportBuilder;
import org.glassfish.grizzly.utils.StringFilter;

import com.grizzly.sample.filter.EchoFilter;

/**
 * Class initializes and starts the echo server, based on Grizzly 2.3
 */
public class EchoServer {

  public static final Logger logger = Logger.getLogger(EchoServer.class.getName());

  public static final String HOST;
  public static final Integer PORT;

  static {
    HOST = Optional.ofNullable(System.getenv("HOSTNAME")).orElse("localhost");
    PORT = Integer.parseInt(Optional.ofNullable(System.getenv("PORT")).orElse("8080"));
  }

  public static void main(String[] args) throws IOException {
    // Create a FilterChain using FilterChainBuilder
    FilterChainBuilder filterChainBuilder = FilterChainBuilder.stateless();

    // Add TransportFilter, which is responsible
    // for reading and writing data to the connection
    filterChainBuilder.add(new TransportFilter());

    // StringFilter is responsible for Buffer <-> String conversion
    filterChainBuilder.add(new StringFilter(Charset.forName("UTF-8")));

    // EchoFilter is responsible for echoing received messages
    filterChainBuilder.add(new EchoFilter());

    // Create TCP transport
    final TCPNIOTransport transport = TCPNIOTransportBuilder.newInstance().build();

    transport.setProcessor(filterChainBuilder.build());

    try {
      // binding transport to start listen on certain host and port
      transport.bind(HOST, PORT);

      logger.info("Starting transport...");

      // start the transport
      transport.start();

      logger.info("Press any key to stop the server...");

      System.in.read();
    } finally {
      logger.info("Stopping transport...");

      // stop the transport
      transport.shutdownNow();

      logger.info("Stopped transport...");
    }
  }

}
