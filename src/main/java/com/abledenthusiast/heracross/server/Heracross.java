package com.abledenthusiast.heracross.server;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.abledenthusiast.heracross.HeracrossProperties;
import com.abledenthusiast.heracross.server.fileservice.FileHandler;
import com.abledenthusiast.heracross.server.fileservice.FileHandlerLocal;
import com.abledenthusiast.heracross.server.media.library.Library;
import com.abledenthusiast.heracross.server.media.library.LibraryNode;
import com.abledenthusiast.heracross.server.media.library.MediaLibrary;
import com.abledenthusiast.heracross.server.media.library.mediafile.MediaFile;
import com.abledenthusiast.heracross.server.media.library.mediafile.MediaFile.MediaFileType;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
public class Heracross {

    private final AtomicLong counter = new AtomicLong();
    private HeracrossProperties properties;
    //TODO: remove filehandler reference
    private FileHandler fileHandler;
    private HeracrossController controller;

    public Heracross() {
        properties = new HeracrossProperties();
        fileHandler = new FileHandlerLocal(properties.getProjectRoot());
        controller = new HeracrossController(properties.getProjectRoot(), new MediaLibrary(
            properties.getProjectRoot(), fileHandler
        ));
    }

    @GetMapping(path = "/ping")
    public String ping() {
        properties.getProjectRoot();
        return properties.getProjectRoot().toString();
    }

    @GetMapping(path = "/version")
    public String version() {
        return properties.getVersion();
    }

    @PostMapping("/upload/series/{seriesName}")
    public String createSeries(@PathVariable("seriesName") String seriesName) {
        //FIXME: change to possible enum for actual reason creation failed
        boolean res = controller.createSeries(seriesName);
        return String.valueOf(res);
    }

    @PostMapping("/upload/movie/{movieName}")
    public void uploadMovie(@RequestParam("file") MultipartFile file,
                              @PathVariable("movieName") String movieName,
                              HttpServletResponse response) {
        //FIXME: change to possible enum for actual reason creation failed
        controller.addSingle(file, movieName, MediaFileType.Movie);
        response.setStatus(201);
    }

    @PostMapping("/upload/tv/{seriesName}")
    public void uploadSeriesFile(@RequestParam("file") MultipartFile file,
                                 @PathVariable("seriesName") String seriesName,
                                 HttpServletResponse response) {
        controller.addSeriesFile(file, seriesName, MediaFileType.TVSeries);
        response.setStatus(201);
    }

    @GetMapping("/getFile/tv/{seriesName}/{episode}")
    public void getSeriesFile(@PathVariable("episode") int episode,
                                 @PathVariable("seriesName") String seriesName,
                                  HttpServletResponse response) {
        LibraryNode libNode = controller.getSeriesMember(seriesName, episode);
        try {
            OutputStream outStream = response.getOutputStream();
            response.setContentType(libNode.file().contentType());
            Files.copy(libNode.path(), outStream);
            response.flushBuffer();
            outStream.close();
        } catch (Exception e) {

        }
    }

    @GetMapping(value="/getFile/movie/{file}")
     public void getMovie(@PathVariable("file") String fileName, HttpServletResponse response) {
        LibraryNode libNode = controller.getSingle(fileName);
        try {
            OutputStream outStream = response.getOutputStream();
            response.setContentType(libNode.file().contentType());
            Files.copy(libNode.path(), outStream);
            response.flushBuffer();
            outStream.close();
        } catch (Exception e) {

        }
    }

    @GetMapping(value="/debug/getFile/all")
     public void getfile(HttpServletRequest request,HttpServletResponse response) throws IOException {
        try {
            OutputStream outStream = response.getOutputStream();
            response.setContentType("text/plain");
            Arrays.asList(properties.getProjectRoot().toFile().listFiles()).stream()
            .map(file -> file.toPath())
            .map(Heracross::probeContentType)
            .forEach(str -> {
                        try {
                            str = str + ", ";
                            outStream.write(str.getBytes());
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    });
            // Files.copy(temp, outStream);
            // Files.probeContentType(temp);
            response.flushBuffer();
            outStream.close();
        } catch (Exception e) {

        }
    }


    private static String probeContentType(Path path) {
        String result = "";
        try {
            result = Files.probeContentType(path);
        } catch(Exception e) {
            System.out.println("error while probing");
        }
        return result;
    }

}