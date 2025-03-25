package com.rahim.proto.config;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Rahim Ahmed
 * @created 16/03/2025
 */
@Configuration
public class GrpcClientConfig {

  @Value("${grpc.server.host}")
  private String grpcServerHost;

  @Value("${grpc.server.port}")
  private int grpcServerPort;

  @Bean
  public ManagedChannel grpChannel() {
    return ManagedChannelBuilder.forAddress(grpcServerHost, grpcServerPort).usePlaintext().build();
  }
}
