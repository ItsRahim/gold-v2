package com.rahim.proto.util;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Rahim Ahmed
 * @created 17/03/2025
 */
public class ProtobufDerSerUtil {
    private static final Logger logger = LoggerFactory.getLogger(ProtobufDerSerUtil.class);

    public static <T extends Message> T deserialiseByteToProtobuf(byte[] data, T defaultInstance) {
        if (data == null) {
            logger.error("Received null byte array for deserialisation.");
            return null;
        }

        try {
            @SuppressWarnings("unchecked")
            T parsedOject = (T) defaultInstance.getParserForType().parseFrom(data);
            return parsedOject;
        } catch (InvalidProtocolBufferException e) {
            logger.error("Failed to parse byte array to Protobuf object of type: {}", defaultInstance.getClass().getSimpleName());
            return null;
        }
    }

    public static byte[] serialiseProtobufToByteArray(Message data) {
        if (data == null) {
            logger.error("Cannot serialize null Protobuf object.");
            return new byte[0];
        }
        return data.toByteArray();
    }
}
