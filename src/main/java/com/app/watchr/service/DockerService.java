package com.app.watchr.service;

import com.app.watchr.Version;
import com.app.watchr.model.ImageTag;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DockerService {
    @Value("${docker.hub.url}")
    private String dockerHubUrl;

    private RestTemplate template;
    private HttpHeaders headers;

    public DockerService() {
        this.template = new RestTemplate();
        this.headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("Accept", "application/json");
    }

    /**
     * Fetches and parses the list of remote docker hub tags into a list
     * of Version objects sorted correctly by semantic version.
     * @return List of Version objects
     */
    public List<Version> getRemoteVersions(final String imageName) {
       ImageTag tags = this.fetchTags(imageName);

       log.debug("Image Tag: {}", tags);
       if(tags != null) {
            return tags.getResults()
               .stream()
               .map(tag -> new Version(tag.getName()))
               .sorted(Version::compareTo)
               .collect(Collectors.toList());
       } else {
           throw new NullPointerException("Image tags from remote repository are null.");
       }
    }
    /**
     * Fetches image tag info from docker hub for the specified image name
     * @param imageName String the image name for the public image to fetch
     * @return ImageTag POJO for retrieving tag information
     */
    private ImageTag fetchTags(final String imageName) {
        if(imageName.isEmpty()) {
            log.error("Cannot fetch tags for an image when the image name is null or blank");
            return null;
        }

        log.debug("Attempting to fetch tags for docker image: {}", imageName);
        HttpEntity<String> requestEntity = new HttpEntity<>("", headers);
        ResponseEntity<String> responseEntity = template.exchange(dockerHubUrl + "/" + imageName + "/tags", HttpMethod.GET, requestEntity, String.class);
        responseEntity.getBody();
        try {
            ImageTag tags = new ObjectMapper().readValue(responseEntity.getBody(), ImageTag.class);
            log.debug("Successfully parsed tags: {}", tags);
            return tags;
        } catch(Exception e) {
            log.error("There was an error reading the response body: {} into the ImageTag class", responseEntity.getBody(), e);
            return null;
        }
    }
}
