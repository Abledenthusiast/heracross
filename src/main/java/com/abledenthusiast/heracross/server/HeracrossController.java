package com.abledenthusiast.heracross.server;


import java.nio.file.Path;
import java.util.Optional;

import com.abledenthusiast.heracross.server.media.library.Library;
import com.abledenthusiast.heracross.server.media.library.LibraryNode;
import com.abledenthusiast.heracross.server.media.library.mediafile.MediaFile;
import com.abledenthusiast.heracross.server.media.library.mediafile.MediaFile.MediaFileType;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.multipart.MultipartFile;

/*
* class controls the input and output of the api
* this will generally (if not always) be the first step in the pipeline of processing
* all HTTP actions will be handled in this class, unless explicitly stated otherwise.
*/

public class HeracrossController {

    private Library<MediaFile> library;
    private Path projectRoot;
    
    
    public HeracrossController(Path projectRoot, Library<MediaFile> library) {
        this.library = library;
        this.projectRoot = projectRoot;
    }

    public LibraryNode getSeriesMember(String seriesName, int index) throws HttpServerErrorException {
        Optional<LibraryNode> libNode = library.getSeriesMember(seriesName, index - 1);
        if (libNode.isPresent()) {
            return libNode.get();
        }
        System.out.println(library.getEntireLibrary().toString());
        throw new HttpServerErrorException(HttpStatus.NOT_FOUND,
        "No item found at index");
    }

    public LibraryNode getSingle(String fileName) throws HttpServerErrorException {
        Optional<LibraryNode> libNode = library.getSingle(fileName);
        if (libNode.isPresent()) {
            return libNode.get();
        }
        System.out.println(library.getEntireLibrary().toString());
        throw new HttpServerErrorException(HttpStatus.NOT_FOUND,
        "No item found at index");
    }

    public <T extends MultipartFile> void addSeriesFile(T file, String seriesName, MediaFileType mediaType) {
        try {
            MediaFile mediaFile = MediaFile.createMediaFile(file.getOriginalFilename(),
                                file.getContentType(), mediaType);
            library.addToSeries(file.getInputStream(), mediaFile, seriesName);
        } catch (Exception err) {
            err.printStackTrace();
            System.out.printf("Hit exception while adding file to library %s %s", err, seriesName);
            System.out.println();
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,
                                                "Error while adding file to library");
        }
    }

    public <T extends MultipartFile> void addSingle(T file, String movieName, MediaFileType mediaType) {
        try {
            MediaFile mediaFile = MediaFile.createMediaFile(movieName,
                                file.getContentType(), mediaType);
            library.addSingle(file.getInputStream(), mediaFile);
        } catch (Exception err) {
            err.printStackTrace();
            System.out.printf("Hit exception while adding file to library %s %s", err, movieName);
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,
                                                "Error while adding file to library");
        }
    }



    /*
     * creates a new directory for a series if the series does not already exist.
     * this will normalize the seriesName to all lower case, so if ths is not the
     * desired behavior, this should be extended in same way to handle that case.
     */
    public boolean createTVSeries(String seriesName) {
        //TODO: move to the fileHandler. this will need to be more generic    
        return true;
    }

    /*
     * creates a new directory for a series if the series does not already exist.
     * this will normalize the seriesName to all lower case, so if ths is not the
     * desired behavior, this should be extended in same way to handle that case.
     */
    public boolean createMovieSeries(String seriesName) {
        //TODO: move to the fileHandler. this will need to be more generic
        return true;
    }

    

}
