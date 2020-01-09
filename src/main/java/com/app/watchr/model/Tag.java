package com.app.watchr.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Tag {
    private String name;
    private List<DockerImage> images;
    private long id;
    private long repository;
    private boolean v2;

    @JsonProperty("last_updated")
    private String lastUpdated;

    @JsonProperty("last_updater")
    private long lastUpdater;

    @JsonProperty("last_updater_username")
    private String lastUpdaterUsername;

    @JsonProperty("image_id")
    private String imageId;

    @JsonProperty("full_size")
    private long fullSize;
}