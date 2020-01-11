package com.app.watchr;

import com.app.watchr.service.DockerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

	private List<Version> tagNames; // Tags should all be semantically versioned

	public static void main(String[] args) {
		SpringApplication.run(Watchr.class, args);
	}

	@PostConstruct
	public void init() {
		log.info("Finding initial tags from the Docker hub repository...");
		this.tagNames = dockerService.getRemoteVersions();
		log.info("Found the following tags from initial query: {}", tagNames);
	}

	@Scheduled(fixedDelayString = "${polling.delay:60}000")
	private void loop() {
		List<Version> latestTags = dockerService.getRemoteVersions();
		log.info("Latest Tags: {}", latestTags);
		// If new tag is here update the container
		if(imageUpdater.shouldUpdate(tagNames, latestTags)) {
			log.info("We need to updated now!");
		} else {
			log.info("No need to update tags are the same");
		}
	}
}
