package com.rahim.proto.util;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Rahim Ahmed
 * @created 17/03/2025
 */
public class ProtobufBuilder {
    private static final Logger logger = LoggerFactory.getLogger(ProtobufBuilder.class);

    public static <T extends Message> T parseProtobuf(byte[] data, T defaultInstance) {
        try {
            @SuppressWarnings("unchecked")
            T parsedOject = (T) defaultInstance.getParserForType().parseFrom(data);
            return parsedOject;
        } catch (InvalidProtocolBufferException e) {
            logger.error("Failed to parse byte array to Protobuf object of type: {}", defaultInstance.getClass().getSimpleName());
            return null;
        }
    }

    public static byte[] serializeProtobufToByteArray(Message data) {
        return data.toByteArray();
    }
}
