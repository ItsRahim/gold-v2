package com.rahim.proto.service;

import com.google.protobuf.Message;
import io.grpc.stub.AbstractStub;

/**
 * @author Rahim Ahmed
 * @created 16/03/2025
 */
public interface IGrpcClientService {

  /**
   * Generic method to send a gRPC request
   *
   * @param stubCreator Function to create a gRPC stub
   * @param request gRPC request object
   * @param call Function reference to the gRPC method
   * @param <S> Type of gRPC Stub
   * @param <T> Type of Request
   * @param <R> Type of Response
   * @return gRPC Response
   */
  <S extends AbstractStub<S>, T, R> R sendRequest(
      GrpcClientService.StubCreator<S> stubCreator,
      T request,
      GrpcClientService.GrpcCall<S, T, R> call);

  /**
   * Sends a gRPC request and returns the response as a JSON string
   *
   * @param stubCreator creator for the gRPC stub
   * @param request the request object
   * @param call the gRPC call function
   * @return JSON string representation of the response
   * @throws Exception if conversion fails
   */
  <S extends AbstractStub<S>, T> String sendRequestAndReturnJson(
      GrpcClientService.StubCreator<S> stubCreator,
      T request,
      GrpcClientService.GrpcCall<S, T, Message> call);
}
