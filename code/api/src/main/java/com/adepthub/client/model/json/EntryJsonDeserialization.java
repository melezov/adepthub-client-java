package com.adepthub.client.model.json;

import java.util.ArrayList;
import java.util.List;

import com.adepthub.client.model.Artifact;
import com.adepthub.client.model.Entry;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

public enum EntryJsonDeserialization implements JsonDeserialization<Entry>{
  INSTANCE;

  @Override
  public Entry fromJson(final JsonObject jo) {
    final String url = jo.get("url").asString();

    final List<Artifact> artifacts = new ArrayList<Artifact>();
    for (final JsonValue jv : jo.get("artifacts").asArray()) {
      artifacts.add(ArtifactJsonDeserialization.INSTANCE.fromJson(jv.asObject()));
    }

    return new Entry(
        url,
        artifacts.toArray(new Artifact[artifacts.size()]));
  }
}
