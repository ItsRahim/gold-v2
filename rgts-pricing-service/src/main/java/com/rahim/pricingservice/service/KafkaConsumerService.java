package com.rahim.pricingservice.service;

import com.rahim.pricingservice.dto.payload.GoldPriceUpdateDTO;
import com.rahim.proto.protobuf.GoldPriceInfo;
import com.rahim.proto.util.ProtobufDerSerUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/** Kafka consumer service for processing gold price updates. */
@Slf4j
@Service
public class KafkaConsumerService {
  @KafkaListener(topics = "gold.price.update", groupId = "gold-price-group")
  public void listen(byte[] data) {
    try {
      GoldPriceInfo goldPriceInfo = deserialiseProtobuf(data);
      if (goldPriceInfo == null) {
        log.error("Failed to deserialise data into GoldPriceInfo");
        return;
      }

      GoldPriceUpdateDTO goldPriceUpdateDTO = GoldPriceUpdateDTO.fromProtobuf(goldPriceInfo);
      if (goldPriceUpdateDTO == null) {
        log.error("Failed to convert GoldPriceInfo to GoldPriceUpdateDTO.");
        return;
      }

      log.info("Gold price update processed: {}", goldPriceUpdateDTO);
    } catch (Exception e) {
      log.error("Failed to process Kafka message", e);
    }
  }

  private GoldPriceInfo deserialiseProtobuf(byte[] data) {
    return ProtobufDerSerUtil.deserialiseByteToProtobuf(data, GoldPriceInfo.getDefaultInstance());
  }
}
