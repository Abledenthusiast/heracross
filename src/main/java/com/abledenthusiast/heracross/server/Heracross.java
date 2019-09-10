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

import javax.annotation.PostConstruct;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.abledenthusiast.heracross.HeracrossConfig;
import com.abledenthusiast.heracross.HeracrossProperties;
import com.abledenthusiast.heracross.server.fileservice.FileHandler;
import com.abledenthusiast.heracross.server.fileservice.FileHandlerLocal;
import com.abledenthusiast.heracross.server.media.library.LibraryNode;
import com.abledenthusiast.heracross.server.media.library.MediaLibrary;
import com.abledenthusiast.heracross.server.media.library.mediafile.MediaFile.MediaFileType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
public class Heracross {
    @Autowired
    private HeracrossConfig config;
    @Autowired
    private HeracrossController controller;

    public Heracross() {
    }

    @GetMapping(path = "/ping")
    public String ping(HttpServletResponse response) {
        response.setStatus(HttpStatus.OK.value());
        return HttpStatus.OK.name();
    }

    @GetMapping(path = "/version")
    public String version() {
        return config.getVersion();
    }

    @PostMapping("/upload/series/tv/{seriesName}")
    public String createTVSeries(@PathVariable("seriesName") String seriesName) {
        //FIXME: change to possible enum for actual reason creation failed
        boolean res = controller.createTVSeries(seriesName);
        return String.valueOf(res);
    }

    @PostMapping("/upload/series/movie/{seriesName}")
    public String createMovieSeries(@PathVariable("seriesName") String seriesName) {
        //FIXME: change to possible enum for actual reason creation failed
        boolean res = controller.createMovieSeries(seriesName);
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

    @GetMapping("/tv/{seriesName}/{episode}")
    public void getTVSeriesFile(@PathVariable("episode") int episode,
                                 @PathVariable("seriesName") String seriesName,
                                  HttpServletResponse response) {
        LibraryNode libNode = controller.getSeriesMember(seriesName, episode);
        try {
            OutputStream outStream = response.getOutputStream();
            response.setContentType(libNode.file().contentType());
            System.out.println(Files.probeContentType(libNode.path()));
            Files.copy(libNode.path(), outStream);
            response.flushBuffer();
            outStream.close();
        } catch (Exception e) {

        }
    }

    @GetMapping(value="/movies/{file}")
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

    @GetMapping("/movies/{seriesName}/{episode}")
    public void getMovieSeriesFile(@PathVariable("episode") int episode,
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

}