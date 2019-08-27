package com.abledenthusiast.heracross.server.media.library;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import com.abledenthusiast.heracross.server.fileservice.FileHandler;
import com.abledenthusiast.heracross.server.media.library.libcollection.MediaCollection;
import com.abledenthusiast.heracross.server.media.library.mediafile.MediaFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchService;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

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
        initLibrary();
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
    
    public void addEntireSeries(String seriesName, List<? extends MediaFile> files) {
        for(MediaFile file : files) {
            library.addToSeries(seriesName, file);
        }
    }

    @Override
    public void createSeries(String seriesName) {
        createSeriesDir(seriesName);
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

    private void initLibrary() {
        /*
        *   Check if project root has already been initialized.
        *   If true, the library needs to be loaded from the files already made available
        */
        if(fileHandler.isDirectory(rootDirectory)) {
            // traverse directory tree and add files to collection
            new FileTree().loadFiles();
        }
    }

    private void createSeriesDir(String seriesName) {
        fileHandler.createDirectory(rootDirectory.resolve(seriesName));
    }

    final private class FileTree {

        public void loadFiles() {
            Set<String> seriesLog = fileHandler.loadLog();
            File cur = fileHandler.getFile(rootDirectory);
            Deque<File> queue = new ArrayDeque<>(); 

            // -------------------------------

            queue.add(cur);
            while(!queue.isEmpty()) {
                if(fileHandler.isDirectory(cur.toPath())) {
                    File[] files = fileHandler.getFiles(cur.toPath());
                    for(File file : files) {
                        queue.add(file);
                    }
                }
        }
    }
}