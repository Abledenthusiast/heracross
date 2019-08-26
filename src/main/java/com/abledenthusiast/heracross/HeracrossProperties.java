package com.abledenthusiast.heracross;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "heracross")
public class HeracrossProperties {

    private String version;

    private Path projectRoot;

    private String defaultPath = "/HeracrossServer/Medialib/Media";

    public String getVersion() {
        if (version == null) {
            version = System.getenv("VERSION");
        }
        return version;
    }

    public Path getProjectRoot() {
        if (projectRoot == null) {
            projectRoot = Path.of(System.getProperty("user.home"), defaultPath).toAbsolutePath();
            try {
                Files.createDirectory(projectRoot);
            } catch(Exception exception) {
                System.out.printf("Warn: while creating home directory for project ran into %s",
                                    exception);
            }
        }
        return projectRoot;
    }

    public String getUserHome() {
        return System.getProperty("user.home"); 
    }


}