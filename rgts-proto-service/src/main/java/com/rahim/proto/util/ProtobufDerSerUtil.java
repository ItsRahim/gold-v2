package com.rahim.proto.util;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Rahim Ahmed
 * @created 17/03/2025
 */
@Slf4j
public class ProtobufDerSerUtil {
  private ProtobufDerSerUtil() {}

  public static <T extends Message> T deserialiseByteToProtobuf(byte[] data, T defaultInstance) {
    if (data == null) {
      log.error("Received null byte array for deserialisation.");
      return null;
    }

    try {
      @SuppressWarnings("unchecked")
      T parsedOject = (T) defaultInstance.getParserForType().parseFrom(data);
      return parsedOject;
    } catch (InvalidProtocolBufferException e) {
      log.error(
          "Failed to parse byte array to Protobuf object of type: {}",
          defaultInstance.getClass().getSimpleName());
      return null;
    }
  }

  public static byte[] serialiseProtobufToByteArray(Message data) {
    if (data == null) {
      log.error("Cannot serialize null Protobuf object.");
      return new byte[0];
    }
    return data.toByteArray();
  }
}
