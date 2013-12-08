package com.adepthub.client.model.json;

import java.util.ArrayList;
import java.util.List;

import com.adepthub.client.hash.SHA256;
import com.adepthub.client.model.Artifact;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

public enum ArtifactJsonDeserialization
    implements JsonDeserialization<Artifact> {

  INSTANCE;

  @Override
  public Artifact fromJson(final JsonObject jo) {
    final List<String> locations = new ArrayList<String>();
    for (final JsonValue jv : jo.get("locations").asArray()) {
      locations.add(jv.asString());
    }

    final long contentLength = jo.get("content-length").asLong();

    final String hashString = jo.get("hash").asString();

    final String filename = jo.get("filename").asString();

    return new Artifact(
        locations.toArray(new String[locations.size()]),
        contentLength,
        new SHA256(hashString),
        filename);
  }
}
