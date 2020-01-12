package com.app.watchr.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Slf4j
@Service
public class CommandService {

    /**
     * Executes a command on the runtime environment
     * @param command String the command to execute
     * @return String output value of the command if it was successful
     * @throws RuntimeException Error message if the command is unsuccessful
     */
    public String exec(final String command) throws RuntimeException {
        final StringBuilder errorBuilder = new StringBuilder();
        final StringBuilder readerBuilder = new StringBuilder();
        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String error;
            String message;
            boolean failed = false;
            while ((error = stdError.readLine()) != null) {
                log.error("Std Error: {}", error);
                errorBuilder.append(error);
                failed = true;
            }

            if(failed) throw new RuntimeException(errorBuilder.toString());

            while ((message = reader.readLine()) != null) {
                log.debug("Std Out: {}", message);
                readerBuilder.append(message);
            }

            return readerBuilder.toString();
        } catch(IOException e) {
            log.error("IOException thrown while attempting to run command: {}", command, e);
            return null;
        }
    }
}
