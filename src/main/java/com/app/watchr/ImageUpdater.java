package com.app.watchr;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class ImageUpdater {

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
}
