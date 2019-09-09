package com.abledenthusiast.heracross.server.media.library;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import com.abledenthusiast.heracross.server.media.library.mediafile.MediaFile;

public interface Library <T extends MediaFile> {
    //public void add(T file);

    /*
    * Adds a new file to a series
    * The media type of the Mediafile dictates under which directory the series will be created
    * e.g tv/
    *
     */
    void addToSeries(InputStream in, T file, String seriesName);

    Optional<LibraryNode> getSeriesMember(String seriesName, int index);

    void addSingle(InputStream in, MediaFile mediaFile);

    Optional<LibraryNode> getSingle(String fileName);

    boolean contains(String mediaName);

    void createSeries(MediaFile.MediaFileType fileType, String seriesName);
    
    List<?> getEntireLibrary();

    Path constructSeriesPath(MediaFile.MediaFileType mediaType, String seriesName);

}