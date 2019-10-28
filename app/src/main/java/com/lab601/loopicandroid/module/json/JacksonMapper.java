package com.lab601.loopicandroid.module.json;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by xingkong on 2018/12/9.
 * Introduction:
 */
public class JacksonMapper {
    private static final ObjectMapper mapper = new ObjectMapper();

    private JacksonMapper() {
    }

    public static ObjectMapper getInstance() {
        return mapper;
    }
}
