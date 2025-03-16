package com.rahim.pricingservice.controller;

import com.google.protobuf.Empty;
import com.rahim.proto.protobuf.GoldPriceResponse;
import com.rahim.proto.protobuf.GoldPriceServiceGrpc;
import com.rahim.proto.service.IGrpcClientService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Rahim Ahmed
 * @created 16/03/2025
 */
@RestController
@RequestMapping("/api/v2/price")
@RequiredArgsConstructor
public class PriceController {
    private static final Logger logger = LoggerFactory.getLogger(PriceController.class);

    private final IGrpcClientService grpcClientService;

    @GetMapping("/test")
    public String test() {
        try {
            GoldPriceResponse response = grpcClientService.sendRequest(
                    GoldPriceServiceGrpc::newBlockingStub,
                    getEmptyRequest(),
                    GoldPriceServiceGrpc.GoldPriceServiceBlockingStub::getGoldPrice
            );
            logger.debug("Received response from gRPC: {}", response);

            return String.format("Source: %s, Price: %f, Datetime: %s",
                    response.getSource(), response.getPrice(), response.getDatetime());
        } catch (Exception e) {
            logger.error(e.getMessage());
            return "Some error occurred";
        }
    }

    private Empty getEmptyRequest() {
        return Empty.getDefaultInstance();
    }
}
