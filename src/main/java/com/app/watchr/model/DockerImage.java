package com.app.watchr.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DockerImage {
    private long size;
    private String digest;
    private String architecture;
    private String os;
    private String variant;
    private String features;

    @JsonProperty("os_version")
    private String osVersion;

    @JsonProperty("os_features")
    private String osFeatures;
}