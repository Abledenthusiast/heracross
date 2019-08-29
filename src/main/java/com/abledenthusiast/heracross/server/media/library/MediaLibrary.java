package com.abledenthusiast.heracross.server.media.library;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import com.abledenthusiast.heracross.server.fileservice.FileHandler;
import com.abledenthusiast.heracross.server.media.library.libcollection.MediaCollection;
import com.abledenthusiast.heracross.server.media.library.mediafile.MediaFile;
import com.abledenthusiast.heracross.server.media.library.mediafile.MediaFile.MediaFileType;

import javax.naming.OperationNotSupportedException;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchService;
import java.util.ArrayDeque;
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
    public void addToSeries(MediaFile mediaFile, String seriesName) {
        /* verify the directory for this series has already been constructed */
        Path seriesDir = constructSeriesPath(mediaFile.getMediaFileType(), seriesName);
        if(!Files.isDirectory(seriesDir)) {
            try {
                Files.createDirectories(seriesDir);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
			}
        }
        System.out.printf("adding to series %s", seriesName);
        commitSeries(seriesName, seriesDir);

        Path filePath = seriesDir.resolve(mediaFile.getName());
        library.addToSeries(seriesName, new LibEntry(mediaFile, filePath));
    }
    
    public void addEntireSeries(String seriesName, List<? extends MediaFile> files) {
        Path seriesDir = constructSeriesPath(files.get(0).getMediaFileType(), seriesName);
        for(MediaFile file : files) {
            Path filePath = seriesDir.resolve(file.getName());
            library.addToSeries(seriesName, new LibEntry(file, filePath));
        }
    }

    @Override
    public void createSeries(MediaFileType mediaType, String seriesName) {
        createSeriesDir(constructSeriesPath(mediaType, seriesName));
        library.createSeries(seriesName);
    }

    @Override
    public Optional<LibraryNode> getSeriesMember(String name, int index) {
        List<LibraryNode> series = library.getSeries(name);
        if(series.size() > index) {
            return Optional.of(series.get(index));
        }
        return Optional.empty();
    }

    @Override
    public void addSingle(MediaFile mediaFile) {
        Path path = constructSinglePath(mediaFile);
        library.addSingle(new LibEntry(mediaFile, path));
    }

    @Override
    public Optional<LibraryNode> getSingle(String fileName) {
        return library.getSingle(fileName);
    }


    /*
    *   Return true if the library contains a series or single instance of this name
    */
    public boolean contains(String name) {
        
        return library.getSeries(name).size() > 0 ||
                library.getSingle(name).isPresent();
    }

    /* this may need to change for a specific impl */
    public List<LibraryNode> getEntireLibrary() {
        List<LibraryNode> complete = library.getEntireCollection();
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
            try {
                loadFromLog();
            } catch (IOException | OperationNotSupportedException e) {
                e.printStackTrace();
            }
        }
    }


    private void createSeriesDir(Path seriesPath) {
        fileHandler.createDirectory(seriesPath);
    }

    @Override
    public Path constructSeriesPath(MediaFileType mediaType, String seriesName) {
        return rootDirectory.resolve(Path.of(mediaType.getDirName(), seriesName));
    }

    private Path constructSinglePath(MediaFile mediaFile) {
        return rootDirectory.resolve(Path.of(mediaFile.getMediaFileType().getDirName(),
                                            mediaFile.getName()));
    }

    public void initMediaTypeDirs() {
        for(MediaFileType mfileType: MediaFileType.values()) {
            fileHandler.createDirectory(rootDirectory.resolve(mfileType.getDirName()));
        }
    }

    private void commitToLog(MediaFile file) {}

    private void commitSeries(String seriesName, Path seriesPath) {
        try {
            fileHandler.writeLog(seriesName + ":" + seriesPath.toString());
        } catch(Exception err) {

        }
    }



    /*
    * This portion should rectify the commit log, library collections and the actual underlying file store
    *
    *
    *
    *
     */
    public void loadFromLog() throws IOException, OperationNotSupportedException {

        Set<String> commitLog = fileHandler.loadLog();
        File cur = fileHandler.getFile(rootDirectory);
        Deque<File> queue = new ArrayDeque<>();

        for (String line : commitLog) {
            /* construct mediaFile from commitlog:
            *   Tuple string will have the structure of: FILE_PATH:NAME:CONTENT_TYPE:MEDIA_FILE_TYPE:SERIES_NAME
            */

            String[] logTuple = line.split(":");
            Path filePath = Path.of(logTuple[0]);
            String fileName = logTuple[1];
            String contentType = logTuple[2];
            MediaFileType mediaType = MediaFileType.valueOf(logTuple[3]);
            String seriesName = logTuple[4];

            MediaFile mediaFile = MediaFile.createMediaFile(fileName,
                    contentType, mediaType);

            if(!seriesName.equals("")) {
                library.addToSeries(seriesName, new LibEntry(mediaFile, filePath));
            } else {
                library.addSingle(new LibEntry(mediaFile, filePath));
            }

        }

        // -------------------------------

    }


    /*private void rectifyCommitLog() throws IOException {

        Set<String> seriesLog = fileHandler.loadLog();
        File cur = fileHandler.getFile(rootDirectory);
        Deque<File> queue = new ArrayDeque<>();

        // -------------------------------

        queue.add(cur);
        while(!queue.isEmpty()) {
            if(fileHandler.isDirectory(cur.toPath())) {
                if(seriesLog.contains(cur.getName())) {
                    library.addToSeries(cur.getName(), file);
                }
                File[] files = fileHandler.getFiles(cur.toPath());
                for(File file : files) {
                    queue.add(file);
                }
            } else {

            }
        }
    }*/

    final private class FileNode {
        public File parent;
    }

    final private static class LibEntry implements LibraryNode {
        private final MediaFile file;
        private final Path path;

        LibEntry(MediaFile file, Path path) {
            this.file = file;
            this.path = path;
        }

        @Override
        public MediaFile file() {
            return file;
        }

        @Override
        public Path path() {
            return path;
        }

        @Override
        public boolean equals(Object var1) {
            if(! (var1 instanceof LibEntry)) {
                return false;
            }
            LibEntry otherEntry = (LibEntry) var1;
            return file.equals(otherEntry.file) && path.equals(otherEntry.path);
        }

        @Override
        public int hashCode() {
            int result = file.hashCode();
            return 31 * result + path.hashCode();
        }



    }


}