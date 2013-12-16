package com.adepthub.client.model.json;

import java.util.ArrayList;
import java.util.List;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

public abstract class JsonDeserializationHelper {
  public static String[] toStringArray(final JsonObject jo, final String property) {
    final List<String> values = new ArrayList<String>();
    for (final JsonValue value : jo.get(property).asArray()) {
      values.add(value.asString());
    }
    return values.toArray(new String[values.size()]);
  }
}
