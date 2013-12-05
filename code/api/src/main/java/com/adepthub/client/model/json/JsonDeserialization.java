package com.adepthub.client.model.json;

import com.eclipsesource.json.JsonObject;

public interface JsonDeserialization<T> {
  public T fromJson(final JsonObject jo);
}
