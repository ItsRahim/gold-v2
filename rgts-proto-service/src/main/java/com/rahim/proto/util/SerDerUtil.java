package com.rahim.proto.util;

import com.google.protobuf.Message;

/**
 * @author Rahim Ahmed
 * @created 17/03/2025
 */
public class SerDerUtil {

    public static byte[] serializeProtobufToByteArray(Message data) {
        return data.toByteArray();
    }
}
