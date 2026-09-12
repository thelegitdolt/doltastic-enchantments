package com.dolthhaven.doltasticenchantments.core.utils;

import com.google.gson.JsonElement;

import java.util.List;

public class JsonUtil {
    public static List<String> listOrSingleton(JsonElement element) {
        List<String> list;
        if (element.isJsonArray()) {
            list = element.getAsJsonArray().asList().stream().map(JsonElement::getAsString).toList();
        } else {
            list = List.of(element.getAsString());
        }
        return list;
    }
}
