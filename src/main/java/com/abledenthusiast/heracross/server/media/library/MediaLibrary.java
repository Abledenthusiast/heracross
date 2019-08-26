package com.abledenthusiast.heracross.server.media.library;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import com.abledenthusiast.heracross.server.fileservice.FileHandler;
import com.abledenthusiast.heracross.server.media.library.libcollection.MediaCollection;
import com.abledenthusiast.heracross.server.media.library.mediafile.MediaFile;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchService;
import java.util.ArrayList;

public class MediaLibrary implements Library<MediaFile> {
    private MediaCollection library;
    private Path rootDirectory;
    private FileHandler<?> fileHandler;
    private WatchService watchService;
    private ScheduledThreadPoolExecutor curators;


    public MediaLibrary(Path rootDirectory, FileHandler<?> fileHandler) {
        curators = new ScheduledThreadPoolExecutor(10);
        watchService = initWatcher();
        library = new MediaCollection();
        this.fileHandler = fileHandler;
        this.rootDirectory = rootDirectory;
    }

    @Override
    public void addToSeries(MediaFile file, String seriesName) {
        Path seriesDir = rootDirectory.resolve(seriesName);
        if(!Files.isDirectory(seriesDir)) {
            try {
                Files.createDirectories(seriesDir);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
			}
        }
        System.out.printf("adding series %s", seriesName);
        library.addToSeries(seriesName, file);
    }
    
    public void addEntireSeries(List<? extends MediaFile> files) {
        for(MediaFile file : files) {
            //library.addToSeries(file);
        }
    }

    @Override
    public Optional<MediaFile> getSeriesMember(String name, int index) {
        List<MediaFile> series = library.getSeries(name);
        if(series.size() > index) {
            return Optional.of(series.get(index));
        }
        return Optional.empty();
    }

    public void put(MediaFile media) {
        library.put(media.getName(), media);
    }


    /*
    *   Return true if the library contains a series or single instance of this name
    */
    public boolean contains(String name) {
        
        return library.getSeries(name).size() > 0 ||
                library.getSingle(name).isPresent();
    }

    /* this may need to change for a specific impl */
    public List<MediaFile> getEntireLibrary() {
        List<MediaFile> complete = library.getEntireCollection();
        return complete;
    }

    public List<?> shuffle() {
        throw new UnsupportedOperationException("Operation currently not supported");
    }


    private WatchService initWatcher() {
        try {
            return FileSystems.getDefault().newWatchService();
        } catch(Exception err) {
            
        }
        return null;
    }
}