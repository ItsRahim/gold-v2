package com.rahim.pricingservice.service;

import com.google.protobuf.Empty;
import com.rahim.proto.protobuf.GoldPriceResponse;
import com.rahim.proto.protobuf.GoldPriceServiceGrpc;
import com.rahim.proto.service.IGrpcClientService;
import io.grpc.stub.StreamObserver;
import jakarta.annotation.PostConstruct;
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
public class GoldPriceStreamingClient {
    private static final Logger logger = LoggerFactory.getLogger(GoldPriceStreamingClient.class);
    private final IGrpcClientService grpcClientService;

    @PostConstruct
    public void init() {
        logger.info("Initialising gRPC Streaming Client for Gold Price updates");

        Empty request = Empty.newBuilder().build();
        GoldPriceServiceGrpc.GoldPriceServiceStub asyncStub = GoldPriceServiceGrpc.newStub(grpcClientService.getChannel());

        asyncStub.streamGoldPrice(request, new StreamObserver<>() {

            @Override
            public void onNext(GoldPriceResponse goldPriceResponse) {
                logger.debug("Received Gold Price Update: {}", goldPriceResponse.getPrice());
            }

            @Override
            public void onError(Throwable throwable) {
                logger.error("Error while receiving gold price updates: {}", throwable.getMessage(), throwable);
            }

            @Override
            public void onCompleted() {
                logger.info("Gold Price Streaming completed.");
            }
        });
    }
}
