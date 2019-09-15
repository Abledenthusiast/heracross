package com.abledenthusiast.heracross;

import java.io.File;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Comparator;

import com.abledenthusiast.heracross.server.HeracrossController;
import com.abledenthusiast.heracross.server.fileservice.FileHandlerLocal;
import com.abledenthusiast.heracross.server.media.library.MediaLibrary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Configuration
public class HeracrossConfig {

    @Autowired
    private HeracrossProperties heracrossProperties;

    private String version;

    private Path projectRoot;

    private String defaultPath = "/HeracrossServer/Medialib/Media";

    private String directoryStore;

    public String getVersion() {
        return heracrossProperties.getVersion();
    }

    public Path getProjectRoot() {
        String root = getRoot();
        if (projectRoot == null) {
            projectRoot = Path.of(root, defaultPath).toAbsolutePath();
            //TODO: move block to file handlers
            try {
                Files.createDirectory(projectRoot);
            } catch(FileAlreadyExistsException ignore) {
                //ignored
            } catch (Exception exception) {
                System.out.printf("Warn: while creating home directory for project ran into %s", exception);
            }
        }
        return projectRoot;
    }

    public String getUserHome() {
        return System.getProperty("user.home");
    }

    private static String getLargestDrive() {
        File[] roots = File.listRoots();
        Arrays.sort(roots, Comparator.comparingLong(File::getTotalSpace));
        return roots[roots.length - 1].getPath();
    }

    private String getRoot() {
        if (directoryStore == null || directoryStore.isEmpty()) {
            directoryStore = "Default";
        }

        PathOpt opt = PathOpt.valueOf(directoryStore);
        switch (opt) {
        case Default:
            return System.getProperty("user.home");
        case DriveOrder:
            return getLargestDrive();
        default:
            throw new IllegalArgumentException("Illegal property detected for directoryStore");
        }
    }

    private enum PathOpt {
        Default, DriveOrder;
    }

    @Bean
    public HeracrossController createController() {
        try {
            return new HeracrossController(getProjectRoot(),
                    new MediaLibrary(getProjectRoot(), new FileHandlerLocal(getProjectRoot())));
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }
}