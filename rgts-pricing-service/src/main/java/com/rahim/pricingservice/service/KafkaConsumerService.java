package com.rahim.pricingservice.service;

import com.rahim.pricingservice.dto.grpc.GoldPriceUpdateDTO;
import com.rahim.proto.protobuf.GoldPriceInfo;
import com.rahim.proto.util.ProtobufDerSerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Kafka consumer service for processing gold price updates.
 */
@Service
public class KafkaConsumerService {
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);

    @KafkaListener(topics = "gold.price.update", groupId = "gold-price-group")
    public void listen(byte[] data) {
        if (data == null) {
            logger.error("Received null data from Kafka.");
            return;
        }

        try {
            logger.debug("Received data: {}", data);
            GoldPriceInfo goldPriceInfo = deserialiseProtobuf(data);

            if (goldPriceInfo == null) {
                logger.error("Failed to deserialise data into GoldPriceInfo");
                return;
            }

            GoldPriceUpdateDTO goldPriceUpdateDTO = GoldPriceUpdateDTO.fromProtobuf(goldPriceInfo);

            if (goldPriceUpdateDTO == null) {
                logger.error("Failed to convert GoldPriceInfo to GoldPriceUpdateDTO.");
                return;
            }

            logger.info("Gold price update processed: {}", goldPriceUpdateDTO);
        } catch (Exception e) {
            logger.error("Failed to process Kafka message", e);
        }
    }

    private GoldPriceInfo deserialiseProtobuf(byte[] data) {
        return ProtobufDerSerUtil.deserialiseByteToProtobuf(data, GoldPriceInfo.getDefaultInstance());
    }
}
