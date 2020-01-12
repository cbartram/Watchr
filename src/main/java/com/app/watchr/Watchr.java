package com.app.watchr;

import com.app.watchr.service.DockerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
import java.util.List;

@Slf4j
@EnableScheduling
@SpringBootApplication
public class Watchr {

	@Autowired
	private DockerService dockerService;

	@Autowired
	private ImageUpdater imageUpdater;

	@Value("${container.name}")
	private String containerName;

	@Value("${image.name}")
	private String imageName;

	private List<Version> tagNames; // Tags should all be semantically versioned

	public static void main(String[] args) {
		SpringApplication.run(Watchr.class, args);
	}

	@PostConstruct
	public void init() {
		log.info("Finding initial tags from the Docker hub repository...");
		this.tagNames = dockerService.getRemoteVersions(imageName);
		log.info("Found the following tags from initial query: {}", tagNames);
	}

	@Scheduled(fixedDelayString = "${polling.delay:60}000")
	private void poll() {
		List<Version> latestTags = dockerService.getRemoteVersions(imageName);
		log.info("Latest Tags: {}", latestTags);
		// If new tag is here update the container
		if(imageUpdater.shouldUpdate(tagNames, latestTags)) {
			log.info("Updating image...");
			imageUpdater.updateContainer(containerName, imageName, imageUpdater.getLatestVersion());
		} else {
			log.info("No need to update. Latest tags are the same");
		}
	}
}
