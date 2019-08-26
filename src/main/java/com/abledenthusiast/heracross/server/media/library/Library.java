package com.abledenthusiast.heracross.server.media.library;

import java.util.List;
import java.util.Optional;

import com.abledenthusiast.heracross.server.media.library.mediafile.MediaFile;

public interface Library <T extends MediaFile> {
    //public void add(T file);
    public void addToSeries(T file, String seriesName);

    public Optional<T> getSeriesMember(String seriesName, int index);
    //public T get(String mediaName);
    public boolean contains(String mediaName);

    public List<?> getEntireLibrary();
}