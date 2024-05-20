package org.example;

import lombok.extern.slf4j.Slf4j;
import org.example.utility.DroolFileInserter;
import org.example.utility.DroolRuleExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class InterpretDroolsFiles {

    private final DroolRuleExtractor droolRuleExtractor;

    private final DroolFileInserter droolFileInserter;
    private static final long INITIAL_DELAY = 0L;
    private static final long PERIOD = 100L; // Check every 100 seconds
    private static final long MAX_RUNTIME = 60L; // Run for 1 minute

    @Autowired
    public InterpretDroolsFiles(DroolRuleExtractor droolRuleExtractor, DroolFileInserter droolFileInserter) {
        this.droolRuleExtractor = droolRuleExtractor;
        this.droolFileInserter = droolFileInserter;
    }

    public void start() {
        String directoryPath = "C:\\Temp\\DroolFiles";
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        Runnable task = () -> checkDirectory(directoryPath);

        executor.scheduleAtFixedRate(task, INITIAL_DELAY, PERIOD, TimeUnit.SECONDS);

        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(MAX_RUNTIME));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interrupted", e);
        } finally {
            executor.shutdownNow();
        }
    }

    private void checkDirectory(String directoryPath) {
        boolean fileFound = false;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(directoryPath), "*.drl")) {
            for (Path entry : stream) {
                fileFound = true;
                processFile(entry.toFile());
            }
            if (!fileFound) {
                throw new IOException("No files found in the directory");
            }
        } catch (IOException e) {
            log.error("Error processing directory", e);
        }
    }

    private void processFile(File file) {
        log.info("Processing file: {}", file.getName());
        try {
            droolRuleExtractor.extractRules(file.getAbsolutePath());
            droolFileInserter.insertDroolFile(file.getName());
            boolean isDeleted = Files.deleteIfExists(file.toPath());
            if (isDeleted) {
                log.info("File deleted successfully: {}", file.getName());
            } else {
                log.error("Failed to delete file: {}", file.getName());
            }
        } catch (IOException e) {
            log.error("Error processing file: {}", file.getName(), e);
        }
    }

}
