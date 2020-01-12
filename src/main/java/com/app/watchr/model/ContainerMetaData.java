package com.app.watchr.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContainerMetaData {
    @JsonProperty("Id")
    private String id;

    @JsonProperty("Name")
    private String name;

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class HostConfig {
        private Map<String, List<Map<String, String>>> PortBindings;
    }
    private Map<String, String> HostConfig;
}
