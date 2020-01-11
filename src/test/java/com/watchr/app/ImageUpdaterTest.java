package com.watchr.app;

import com.app.watchr.ImageUpdater;
import com.app.watchr.Version;
import com.app.watchr.service.DockerService;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ImageUpdaterTest {

	@Test
	void itShouldNotUpdateIfNoNewTagsArePublished() {
		List<Version> currentImages = new ArrayList<>();

		currentImages.add(new Version("1.0.2"));
		currentImages.add(new Version("1.0.1"));
		currentImages.add(new Version("1.1.6"));
		currentImages.add(new Version("1.0.3"));
		currentImages.add(new Version("1.0.6"));
		currentImages.add(new Version("2.0.1"));

		DockerService dockerService = mock(DockerService.class);
		when(dockerService.getRemoteVersions()).thenReturn(currentImages);

		ImageUpdater imageUpdater = new ImageUpdater(dockerService);
		Assert.assertFalse(imageUpdater.shouldUpdate(currentImages, currentImages));
	}

	@Test
	void itShouldReturnTrueWhenNewTagIsFound() {
		List<Version> currentImages = new ArrayList<>();

		// Current images comes in sorted order
		currentImages.add(new Version("1.0.1"));
		currentImages.add(new Version("1.0.2"));
		currentImages.add(new Version("1.0.3"));
		currentImages.add(new Version("1.0.6"));
		currentImages.add(new Version("1.1.6"));
		currentImages.add(new Version("2.0.1"));


		List<Version> latestImages = new ArrayList<>(currentImages);

		// Add 1 more later version to latest images
		latestImages.add(new Version("2.0.2"));

		DockerService dockerService = mock(DockerService.class);
		when(dockerService.getRemoteVersions()).thenReturn(latestImages);

		ImageUpdater imageUpdater = new ImageUpdater(dockerService);
		Assert.assertTrue(imageUpdater.shouldUpdate(currentImages, latestImages));
	}


	@Test
	void itShouldReturnFalseWhenNewLatestImageIsNotTheMostRecent() {
		List<Version> currentImages = new ArrayList<>();

		// Current images comes in sorted order
		currentImages.add(new Version("1.0.1"));
		currentImages.add(new Version("1.0.2"));
		currentImages.add(new Version("1.0.3"));
		currentImages.add(new Version("1.0.6"));
		currentImages.add(new Version("1.1.6"));
		currentImages.add(new Version("2.0.1"));


		List<Version> latestImages = new ArrayList<>(currentImages);

		// Add 1 more later version to latest images but its not the "latest" version present
		latestImages.add(new Version("1.1.5"));

		DockerService dockerService = mock(DockerService.class);
		when(dockerService.getRemoteVersions()).thenReturn(latestImages);

		ImageUpdater imageUpdater = new ImageUpdater(dockerService);
		Assert.assertFalse(imageUpdater.shouldUpdate(currentImages, latestImages));
	}

}
