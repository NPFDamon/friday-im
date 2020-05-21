package com.friday.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.util.JsonFormat;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * Copyright (C),Damon
 *
 * @Description:
 * @Author: Damon(npf)
 * @Date: 2020-05-18:10:38
 */
@Slf4j
public class JsonHelper {
    private static final ObjectMapper mapper = new ObjectMapper();

    private static final JsonFormat.Parser parser = JsonFormat.parser();

    private static final JsonFormat.Printer printer = JsonFormat.printer().includingDefaultValueFields().printingEnumsAsInts();


    public static String toJsonString(Object o) {
        try {
            return mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            log.error("to json string error", e);
        }
        return null;
    }

    public static byte[] toJsonByte(Object o) {
        try {
            return mapper.writeValueAsBytes(o);
        } catch (JsonProcessingException e) {
            log.error("to json byre error", e);
        }
        return null;
    }

    public static Map<String, Object> strToMap(String s) {
        try {
            return mapper.readValue(s, Map.class);
        } catch (JsonProcessingException e) {
            log.error("read json value error", e);
        }
        return null;
    }

    public static <T> T readValue(String s, Class<T> clazz) {
        try {
            return mapper.readValue(s, clazz);
        } catch (JsonProcessingException e) {
            log.error("read json value error", e);
        }
        return null;
    }

    public static void readValue(String json, Message.Builder builder) {
        try {
            parser.merge(json, builder);
        } catch (InvalidProtocolBufferException e) {
            log.error("read pb json value error", e);
        }
    }

    public static String toJsonString(MessageOrBuilder builder) {
        try {
            return printer.print(builder);
        } catch (InvalidProtocolBufferException e) {
            log.error("pb to json string error", e);
        }
        return null;
    }


}
