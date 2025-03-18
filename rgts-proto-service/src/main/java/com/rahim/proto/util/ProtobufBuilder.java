package com.rahim.proto.util;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.rahim.proto.protobuf.GoldPriceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Rahim Ahmed
 * @created 17/03/2025
 */
public class ProtobufBuilder {
    private static final Logger logger = LoggerFactory.getLogger(ProtobufBuilder.class);

    public static GoldPriceInfo parseGoldPriceInfo(byte[] data) {
        try {
            return GoldPriceInfo.parseFrom(data);
        } catch (InvalidProtocolBufferException e) {
            logger.error("Failed to parse byte array to GoldPriceInfo", e);
            return null;
        }
    }

    public static byte[] serializeProtobufToByteArray(Message data) {
        return data.toByteArray();
    }
}
