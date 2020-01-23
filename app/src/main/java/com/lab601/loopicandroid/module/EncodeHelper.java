package com.lab601.loopicandroid.module;

import java.util.Base64;

public class EncodeHelper {

    public static String decodeBase64(String data) {
        return new String(Base64.getDecoder().decode(data.getBytes()));
    }
}
