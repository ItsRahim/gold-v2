package com.rahim.proto.service;

import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import com.rahim.proto.exception.GrpcRequestException;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.AbstractStub;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author Rahim Ahmed
 * @created 16/03/2025
 */
@Service
@RequiredArgsConstructor
public class GrpcClientService implements IGrpcClientService {
  private static final Logger logger = LoggerFactory.getLogger(GrpcClientService.class);
  private final ManagedChannel channel;

  @Override
  public <S extends AbstractStub<S>, T, R> R sendRequest(
      StubCreator<S> stubCreator, T request, GrpcCall<S, T, R> call) {
    logger.debug("Sending gRPC request: {}", request);
    S stub = stubCreator.create(channel);
    try {
      R response = call.execute(stub, request);
      logger.debug("Received gRPC response: {}", response);
      return response;
    } catch (StatusRuntimeException e) {
      logger.error("gRPC request failed: {}. Status: {}", e.getStatus(), e.getMessage());
      throw new GrpcRequestException("gRPC request failed", e);
    } catch (Exception e) {
      logger.error("An error occurred while sending gRPC request: {}", e.getMessage(), e);
      throw new GrpcRequestException("An unexpected error occurred while sending gRPC request", e);
    }
  }

  public <S extends AbstractStub<S>, T> String sendRequestAndReturnJson(
      StubCreator<S> stubCreator, T request, GrpcCall<S, T, Message> call) {
    try {
      Message response = sendRequest(stubCreator, request, call);
      return protoToJson(response);
    } catch (GrpcRequestException e) {
      logger.error("Error occurred during gRPC request execution: {}", e.getMessage(), e);
      return null;
    } catch (Exception e) {
      logger.error(
          "Unexpected error during gRPC request and JSON conversion: {}", e.getMessage(), e);
      return null;
    }
  }

  /**
   * Converts a Protocol Buffer message to a JSON string
   *
   * @param protoMessage The Protocol Buffer message to convert
   * @return JSON string representation of the message
   */
  private String protoToJson(Message protoMessage) {
    try {
      return JsonFormat.printer().alwaysPrintFieldsWithNoPresence().print(protoMessage);
    } catch (Exception e) {
      logger.error(
          "An error occurred while converting protobuf object to JSON: {}", e.getMessage(), e);
      throw new GrpcRequestException(
          "An error occurred while converting protobuf object to JSON", e);
    }
  }

  @FunctionalInterface
  public interface StubCreator<S> {
    S create(ManagedChannel channel);
  }

  @FunctionalInterface
  public interface GrpcCall<S, T, R> {
    R execute(S stub, T request);
  }
}
