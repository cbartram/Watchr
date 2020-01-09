package com.app.watchr.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImageTag {
    private int count;
    private String next;
    private String previous;
    private List<Tag> results;
}
