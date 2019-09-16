package com.moufee.boilerfit.api;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.lang.reflect.Type;

/**
 * Handles conversion of time string to and from LocalTime (for meal serving times and Hours)
 */

public class LocalTimeTypeConverter implements JsonSerializer<LocalTime>, JsonDeserializer<LocalTime> {

    private DateTimeFormatter mDateTimeFormatter = DateTimeFormat.forPattern("kk:mm:ss");

    @Override
    public LocalTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return mDateTimeFormatter.parseLocalTime(json.getAsString().substring(0, 8));
    }

    @Override
    public JsonElement serialize(LocalTime src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(mDateTimeFormatter.print(src));
    }
}
