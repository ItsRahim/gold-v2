package com.rahim.proto.service;

import io.grpc.ManagedChannel;
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
    public <S extends AbstractStub<S>, T, R> R sendRequest(StubCreator<S> stubCreator, T request, GrpcCall<S, T, R> call) {
        logger.debug("Sending gRPC request: {}", request);
        S stub = stubCreator.create(channel);

        R response = call.execute(stub, request);
        logger.debug("Received gRPC response: {}", response);

        return response;
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
