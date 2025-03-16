package com.rahim.proto.service;

import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
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

    public <S extends AbstractStub<S>, T> String sendRequestAndReturnJson(StubCreator<S> stubCreator, T request, GrpcCall<S, T, Message> call) {
        try {
            Message response = sendRequest(stubCreator, request, call);
            return protoToJson(response);
        } catch (Exception e) {
            logger.error("An error occurred while sending gRPC request and converting to JSON", e);
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
            return JsonFormat.printer()
                    .alwaysPrintFieldsWithNoPresence()
                    .print(protoMessage);
        } catch (Exception e) {
            logger.error("An error occurred while converting protobuf object to JSON", e);
            return null;
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
