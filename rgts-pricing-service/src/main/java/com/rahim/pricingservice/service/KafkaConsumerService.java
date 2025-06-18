package com.rahim.pricingservice.service;

import com.rahim.pricingservice.dto.payload.GoldPriceUpdateDTO;
import com.rahim.proto.protobuf.price.GoldPriceInfo;
import com.rahim.proto.util.ProtobufDerSerUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

/** Kafka consumer service for processing gold price updates. */
@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {
  private final IUpdateGoldPriceService updateGoldPriceService;

  @KafkaListener(topics = "gold.price.update", groupId = "gold-price-group")
  public void listen(@Payload byte[] data, Acknowledgment ack) {
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

      updateGoldPriceService.updateBasePrice(goldPriceUpdateDTO);
      ack.acknowledge();
    } catch (Exception e) {
      log.error("Failed to process Kafka message", e);
    }
  }

  private GoldPriceInfo deserialiseProtobuf(byte[] data) {
    return ProtobufDerSerUtil.deserialiseByteToProtobuf(data, GoldPriceInfo.getDefaultInstance());
  }
}
