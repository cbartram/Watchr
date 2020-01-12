package com.app.watchr;

import com.app.watchr.service.DockerService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ImageUpdater {

    @Autowired
    private DockerService dockerService;

    private final ObjectMapper mapper = new ObjectMapper();
    private String containerMetadata;

    public void updateImage(final String containerName, final String imageName, final Version version) {
        String containerId = dockerService.getContainerId(containerName);
        this.containerMetadata = dockerService.getMetaData(containerId);
        try {
            Process process = Runtime.getRuntime().exec(getRunCommand(containerName,  imageName, version, getContainerPort(), getEnv()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String error;
            while ((error = stdError.readLine()) != null) {
                log.error("Std Error: {}", error);
            }
        } catch(IOException e) {
            log.error("IOException thrown while attempting to start new container", e);
        }
    }

    /**
     * Builds the run command used to start the container
     * @return String full run command which can be executed by the current runtime environment
     */
    private String getRunCommand(final String containerName, final String imageName, final Version version, final String port, final String environmentVariables) {
        return "docker run -d --name " + containerName + " -p " + port + " " + environmentVariables + "" + imageName + ":" + version.getVersion();
    }

    /**
     * Returns true if the docker image currently running needs to update because a later tag
     * has been pushed to the repo
     * @param currentTags List of current versions that were tagged since we last polled the server
     * @param latestTags List of the latest version that were recently polled
     * @return False if the images are the same and true if the images should update to the latest version
     */
    public boolean shouldUpdate(final List<Version> currentTags, final List<Version> latestTags) {
        log.info("Latest Tags: {}", latestTags);
        log.info("Current Tags: {}", currentTags);

        // Compare old tags against found new tags
        if(latestTags.size() != currentTags.size()) {
            log.info("New docker images found! Latest Tags: {} Old Tags: {}", latestTags, currentTags);
            // [1.0.8, 1.0.9, 1.0.10] [1.0.8, 1.0.9]
            Version lastLatest = latestTags.get(latestTags.size() - 1);
            Version lastPrevious = currentTags.get(currentTags.size() - 1);
            return lastLatest.compareTo(lastPrevious) >= 1;
        } else {
            log.info("Tag Sizes are equal no new images have been published. Sleeping...");
            return false;
        }
    }

    /**
     * Parses the environmental variables into
     * @return String the environmental variable string pre-formatted for a docker cli call
     */
    private String getEnv() {
        try {
            JsonNode env = this.mapper.readTree(this.containerMetadata.replaceAll("^\\[|]$", ""))
                    .get("Config")
                    .get("Env");

            return this.mapper.readValue(env.toString(), ArrayList.class).stream().map(e -> "-e " + e).collect(Collectors.joining(" ")).toString();
        } catch(IOException e) {
            log.error("IOException thrown while attempting to retrieve running environmental variables ", e);
            return null;
        }
    }

    /**
     * Retrieves the container port that the running image is using.
     * @return String containing the container port
     */
    private String getContainerPort() {
        try {
            JsonNode portBindings = this.mapper.readTree(this.containerMetadata.replaceAll("^\\[|]$", ""))
                    .get("HostConfig")
                    .get("PortBindings");

            log.debug("Retrieved port bindings: {}", portBindings.toString());

            HashMap<String, List<HashMap<String, String>>> ports = this.mapper.readValue(portBindings.toString(), HashMap.class);
            final String port = ports.keySet().stream().map(key -> ports.get(key).get(0).get("HostPort")).collect(Collectors.joining());

            log.debug("Parsed port for nested json object: {}", port);

            return port + ":" + port;
        } catch(IOException e) {
            log.error("IOException thrown while attempting to retrieve running container ports ", e);
            return null;
        }
    }
}