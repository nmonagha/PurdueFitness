package com.moufee.boilerfit.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.moufee.boilerfit.menus.Allergens;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

public class AllergensTypeConverter implements JsonDeserializer<Allergens>, JsonSerializer<Allergens> {
    @Override
    public Allergens deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonArray allergensArray = json.getAsJsonArray();
        Set<String> allergens = new HashSet<>();
        for (int i = 0; i < allergensArray.size(); i++) {
            JsonObject current = allergensArray.get(i).getAsJsonObject();
            if (current.get("Value").getAsBoolean()) {
                allergens.add(current.get("Name").getAsString());
            }

        }
        return new Allergens(allergens);
    }

    @Override
    public JsonElement serialize(Allergens src, Type typeOfSrc, JsonSerializationContext context) {
        JsonArray allergensArray = new JsonArray();
        for (String allergen : src.getAllergenSet()) {
            JsonObject object = new JsonObject();
            object.addProperty("Value", true);
            object.addProperty("Name", allergen);
            allergensArray.add(object);
        }
        return allergensArray;
    }
}
