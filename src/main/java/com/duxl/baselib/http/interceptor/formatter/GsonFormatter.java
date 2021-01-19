package com.duxl.baselib.http.interceptor.formatter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

class GsonFormatter extends JSONFormatter {
    private final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().serializeNulls().create();
    private final JsonParser PARSER = new JsonParser();

    @Override
    String format(String source) {
        return GSON.toJson(PARSER.parse(source));
    }

    static JSONFormatter buildIfSupported() {
        try {
            Class.forName("com.google.gson.Gson");
            return new GsonFormatter();
        } catch (ClassNotFoundException ignore) {
            return null;
        }
    }

}