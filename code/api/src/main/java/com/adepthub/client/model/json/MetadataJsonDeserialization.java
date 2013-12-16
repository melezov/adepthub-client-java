package com.adepthub.client.model.json;

import com.adepthub.client.model.Metadata;
import com.eclipsesource.json.JsonObject;

public enum MetadataJsonDeserialization
    implements JsonDeserialization<Metadata> {

  INSTANCE;

  @Override
  public Metadata fromJson(final JsonObject jo) {
    final String[] organization = JsonDeserializationHelper.toStringArray(jo, "organization");
    final String[] name = JsonDeserializationHelper.toStringArray(jo, "name");
    final String[] version = JsonDeserializationHelper.toStringArray(jo, "version");

    return new Metadata(
        organization,
        name,
        version);
  }
}
