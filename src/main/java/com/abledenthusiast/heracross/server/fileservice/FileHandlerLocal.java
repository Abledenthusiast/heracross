package com.abledenthusiast.heracross.server.fileservice;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.abledenthusiast.heracross.server.media.library.mediafile.MediaFile;
import com.abledenthusiast.heracross.server.media.library.mediafile.MediaFile.MediaFileType;

import org.springframework.web.multipart.MultipartFile;

public class FileHandlerLocal implements FileHandler<MultipartFile> {
    private final static String LOG_FILE = "series.log";
    private Path defaultDir;
    private HashMap<String, String> hashMap;

    public FileHandlerLocal(Path projectRoot) {
        this.defaultDir = projectRoot;
        hashMap = new HashMap<>();
    }
    

    @Override
    public boolean writeFile(MultipartFile file) {
        System.out.print("made it here.");
        Objects.requireNonNull(file);
        boolean result = false;
        Path targetLocation = defaultDir.resolve(file.getOriginalFilename());

        try {
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            result = true;
        } catch (Exception e) {
            System.out.printf("error when writing file %s", e);
            result = false;
        }

        return result;
    }

    // @Override
    // public InputStream getFile(MediaFile mediaFile) throws IOException {
    //     return Files.newInputStream(mediaFile.getPath());

    // }

    // public Path cleanPath(String originalFileName) {
    //     if(originalFileName.contains("/")) {
    //         //FIXME
    //         System.out.println("file contains path info");
    //     }
    //     return Paths.get(defaultDir, originalFileName);
    // } 
    
    public void initDirectory(Path dir) {
        /*
        *   check if the directory already exists
        *   if it does, the library needs to be initialized
        */
        try {
            Files.createDirectories(dir);
        } catch (Exception e) {
            System.out.printf("error when creating directory %s", e);
        }
    }

    /*
    * persist the map to a log file. The file wil be opened and then closed via this method.
    * This should be fine temporarily, since uploading a video should be a fairly rare task anyway 
    * Should upload become a pain point, this method should be checked.
    */
    // public void persist() throws IOException {
    //     try(BufferedWriter writer = Files.newBufferedWriter(persistFile, StandardOpenOption.WRITE,
    //                                                          StandardOpenOption.APPEND)) {
    //         hashMap.entrySet().stream().filter(isValid).forEach(entry -> {
    //             try {
    //                 writer.append(entry.getKey() +":"+entry.getValue());
    //             } catch(IOException e) {

    //             }
    //         });                                                             
    //     }
    // }


    @Override
    public boolean createDirectory(Path directory) {
        try {
            Files.createDirectories(defaultDir.resolve(directory));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean isDirectory(Path directory) {
        return Files.isDirectory(directory);
    }

    @Override
    public File getFile(Path directory) {
        return directory.toFile();
    }

    public File[] getFiles(Path directory) {
        //  @see Files.newDirectoryStream
        return directory.toFile().listFiles();
    }

    @Override
    public void writeLog(String logstr) throws IOException {
        //this write method defaults to UTF-8
        //FIXME: \n should be replaced with systen independent newline
        byte[] toOut = (logstr + "\n").getBytes();
        Files.write(defaultDir.resolve(LOG_FILE), toOut, StandardOpenOption.APPEND);
    }

    @Override
    public Set<String> loadLog() throws IOException {
        return Files.newBufferedReader(defaultDir.resolve(LOG_FILE)).lines()
        .map(line -> line.strip())
        .collect(Collectors.toSet());
	}

}