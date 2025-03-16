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

import java.util.HashMap;
import java.util.Map;

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
    public Map<String, Object> test() {
        try {
            GoldPriceResponse goldPriceResponse = grpcClientService.sendRequest(
                    GoldPriceServiceGrpc::newBlockingStub,
                    getEmptyRequest(),
                    GoldPriceServiceGrpc.GoldPriceServiceBlockingStub::getGoldPrice
            );
            logger.debug("Received response from gRPC: {}", goldPriceResponse);

            Map<String, Object> response = new HashMap<>();
            response.put("Source", goldPriceResponse.getSource());
            response.put("Price", goldPriceResponse.getPrice());
            response.put("Request Time", goldPriceResponse.getDatetime());

            return response;
        } catch (Exception e) {
            logger.error("Error occurred: {}", e.getMessage());
            return null;
        }
    }

    private Empty getEmptyRequest() {
        return Empty.getDefaultInstance();
    }
}
