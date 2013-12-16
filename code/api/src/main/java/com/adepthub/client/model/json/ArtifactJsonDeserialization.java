package com.adepthub.client.model.json;

import com.adepthub.client.hash.SHA256;
import com.adepthub.client.model.Artifact;
import com.adepthub.client.model.Metadata;
import com.eclipsesource.json.JsonObject;

public enum ArtifactJsonDeserialization
    implements JsonDeserialization<Artifact> {

  INSTANCE;

  @Override
  public Artifact fromJson(final JsonObject jo) {
    final String[] locations = JsonDeserializationHelper.toStringArray(jo, "locations");
    final String hashString = jo.get("hash").asString();
    final long size = jo.get("size").asLong();
    final String filename = jo.get("filename").asString();
    final Metadata metadata = MetadataJsonDeserialization.INSTANCE.fromJson(jo.get("metadata").asObject());

    return new Artifact(
        locations,
        new SHA256(hashString),
        size,
        filename,
        metadata);
  }
}
