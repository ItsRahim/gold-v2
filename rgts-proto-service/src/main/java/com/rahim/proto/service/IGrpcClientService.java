package com.rahim.proto.service;

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
    <S extends AbstractStub<S>, T, R> R sendRequest(GrpcClientService.StubCreator<S> stubCreator, T request, GrpcClientService.GrpcCall<S, T, R> call);
}
