package com.abledenthusiast.heracross.server.media.library;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import com.abledenthusiast.heracross.server.media.library.mediafile.MediaFile;

public interface Library <T extends MediaFile> {
    //public void add(T file);
    void addToSeries(T file, String seriesName);

    Optional<LibraryNode> getSeriesMember(String seriesName, int index);

    void addSingle(MediaFile mediaFile);

    Optional<LibraryNode> getSingle(String fileName);

    boolean contains(String mediaName);

    void createSeries(MediaFile.MediaFileType fileType, String seriesName);
    
    List<?> getEntireLibrary();

    Path constructSeriesPath(MediaFile.MediaFileType mediaType, String seriesName);

}