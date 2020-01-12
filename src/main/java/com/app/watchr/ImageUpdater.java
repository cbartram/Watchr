package com.app.watchr;

import com.app.watchr.service.CommandService;
import com.app.watchr.service.DockerService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ImageUpdater {

    @Autowired
    private DockerService dockerService;

    @Autowired
    private CommandService commandService;

    private final ObjectMapper mapper = new ObjectMapper();
    private String containerMetadata;

    @Getter
    private Version latestVersion;

    /**
     * Stops a currently running container given either the container name or id
     * @param containerName String container name or id
     * @return Boolean True if the container was stopped successfully and false otherwise
     */
    public boolean stopContainer(final String containerName) {
        try {
            log.info("Attempting to stop container: {}", containerName);
            commandService.exec("docker container stop " + containerName);
            log.info("Container: {} stopped successfully.", containerName);
            return true;
        } catch(RuntimeException e) {
            log.error("Runtime exception thrown while attempting to stop container: {}", containerName, e);
            return false;
        }
    }

    /**
     * Deletes a previously stopped container freeing up the container name to be used by
     * a new container
     * @param containerName String container name
     * @return Boolean true if the container was deleted successfully and false otherwise
     */
    public boolean deleteContainer(final String containerName) {
        try {
            log.info("Attempting to delete container: {}", containerName);
            commandService.exec("docker container rm " + containerName);
            log.info("Container: {} deleted successfully.", containerName);
            return true;
        } catch(RuntimeException e) {
            log.error("Runtime exception thrown while attempting to delete container: {}", containerName, e);
            return false;
        }
    }

    /**
     * Updates an image to the latest version of itself using semantic versioning.
     * @param containerName String the container name to run the container as (same as currently running container name)
     * @param imageName String the image name for this image from docker hub.
     * @param version String the new version of this container to start
     */
    public boolean startContainer(final String containerName, final String imageName, final Version version) {
        String containerId = dockerService.getContainerId(containerName);
        this.containerMetadata = dockerService.getMetaData(containerId);
        try {
            final String run = getRunCommand(containerName,  imageName, version, getContainerPort(), getEnv());
            log.info("Attempting to start new container using command: {}", run);
            commandService.exec(run);
            log.info("Container {} has been started successfully!", containerName);
            return true;
        } catch(RuntimeException e) {
            log.error("Runtime exception thrown while attempting to start new container: {}", containerName, e);
            return false;
        }
    }

    /**
     * Helper method for stopping, removing, and re-starting a new container
     * @param containerName String container name to create
     * @param imageName String image name to update from
     * @param version Version the version to deploy (start)
     * @return Boolean true if the container can be updated and false otherwise
     */
    public boolean updateContainer(final String containerName, final String imageName, final Version version) {
        if(stopContainer(containerName)) {
            if (deleteContainer(containerName)) {
               if(startContainer(containerName, imageName, version)) {
                   log.info("Started@V{}", version.getVersion());
                   return true;
               } else {
                   log.error("Failed to start new container: {} see additional error logging above", containerName);
                   return false;
               }
            } else {
                log.error("Failed to delete existing container: {} see additional error logging above.", containerName);
                return false;
            }
        } else {
            log.error("Failed to update container: {} see additional error logging above.", containerName);
            return false;
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
            if(lastLatest.compareTo(lastPrevious) >= 1) {
                this.latestVersion = lastLatest;
                return true;
            } else {
                return false;
            }
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