package com.abledenthusiast.heracross.server.fileservice;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Predicate;

import com.abledenthusiast.heracross.server.media.library.mediafile.MediaFile;
import com.abledenthusiast.heracross.server.media.library.mediafile.MediaFile.MediaFileType;

import org.springframework.web.multipart.MultipartFile;

public class FileHandlerLocal implements FileHandler<MultipartFile> {
    private final static String MAP_FILE = "video_map.json";
    private Path defaultDir;
    private HashMap<String, String> hashMap;

    public FileHandlerLocal(Path defaultPath) {
        this.defaultDir = defaultPath;
        hashMap = new HashMap<>();
        initDirectory(defaultPath);
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
    
    public void initDirectory(Path Path) {
        try {
            Files.createDirectories(Path);
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



    public boolean createDirectory(String seriesName) {
        try {
            Files.createDirectories(defaultDir.resolve(seriesName));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }
}