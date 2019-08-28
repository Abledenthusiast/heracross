package com.abledenthusiast.heracross.server;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

import com.abledenthusiast.heracross.server.fileservice.FileHandler;
import com.abledenthusiast.heracross.server.fileservice.FileHandlerLocal;
import com.abledenthusiast.heracross.server.media.library.Library;
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
    private FileHandler fileHandler;
    private Path projectRoot;
    
    
    public HeracrossController(Path projectRoot, Library<MediaFile> library) {
        this.library = library;
        this.projectRoot = projectRoot;
        fileHandler = new FileHandlerLocal(projectRoot);
    }


    /*
    * creates a new directory for a series if the series does not already exist.
    * this will normalize the seriesName to all lower case, so if ths is not the
    * desired behavior, this should be extended in same way to handle that case.
    */
    public boolean createSeries(String seriesName) {
        seriesName = seriesName.toLowerCase();
        if(library.contains(seriesName)) {
            return false;
        }
        return createSeriesTV(seriesName);
    }

    public Path getSeriesMember(String seriesName, int index) {
        Optional<MediaFile> file = library.getSeriesMember(seriesName, index - 1);
        if (file.isPresent()) {
            return file.get().getFilePath();
        }
        System.out.println(library.getEntireLibrary().toString());
        throw new HttpServerErrorException(HttpStatus.NOT_FOUND,
        "No item found at index");
    }

    public <T extends MultipartFile> void addSeriesFile(T file, String seriesName, MediaFileType mediaType) {
        try {
            MediaFile mediaFile = MediaFile.createMediaFile(library.constructSeriesPath(mediaType, seriesName), file.getOriginalFilename(),
                                file.getContentType(), mediaType);
            library.addToSeries(mediaFile, seriesName);
            Files.copy(file.getInputStream(), mediaFile.getFilePath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception err) {
            System.out.printf("Hit exception while adding file to library %s %s", err, seriesName);
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,
                                                "Error while adding file to library");
        }
    }



    /*
     * creates a new directory for a series if the series does not already exist.
     * this will normalize the seriesName to all lower case, so if ths is not the
     * desired behavior, this should be extended in same way to handle that case.
     */
    public boolean createSeriesTV(String seriesName) {
        //TODO: move to the fileHandler. this will need to be more generic
        library.createSeries( MediaFileType.TVSeries, seriesName);
        return true;
    }

}
