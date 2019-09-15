package com.abledenthusiast.heracross.server.media.library;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.abledenthusiast.heracross.server.fileservice.FileHandler;
import com.abledenthusiast.heracross.server.fileservice.dto.MediaDTO;
import com.abledenthusiast.heracross.server.media.library.libcollection.MediaCollection;
import com.abledenthusiast.heracross.server.media.library.mediafile.MediaFile;
import com.abledenthusiast.heracross.server.media.library.mediafile.MediaFile.MediaFileType;

import javax.naming.OperationNotSupportedException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchService;
import java.util.ArrayDeque;
import java.util.Deque;

public class MediaLibrary implements Library<MediaFile> {
    private MediaCollection library;
    private Path rootDirectory;
    private FileHandler fileHandler;
    private WatchService watchService;
    private ScheduledThreadPoolExecutor curators;

    public MediaLibrary(Path rootDirectory, FileHandler fileHandler) {
        curators = new ScheduledThreadPoolExecutor(10);
        library = new MediaCollection();
        this.fileHandler = fileHandler;
        this.rootDirectory = rootDirectory;
        initLibrary();
    }

    @Override
    public void addToSeries(InputStream in, MediaFile mediaFile, String seriesName) {
        /* verify the directory for this series has already been constructed */
        Path seriesDir = constructSeriesPath(mediaFile.getMediaFileType(), seriesName);
        if (library.getSeries(seriesName) == null) {
            System.out.println("series was not found in collection, attempting to create the series");
            if (Files.isDirectory(seriesDir)) {
                try {
                    Files.createDirectories(seriesDir);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        System.out.printf("adding to series %s", seriesName);

        Path filePath = seriesDir.resolve(mediaFile.getName());
        addToSeries(seriesName, new LibEntry(mediaFile, filePath));


        /*
         * Time to actually persist the file to the file store e.g. local file system,
         * gcp, aws, azure
         */
        fileHandler.writeFile(in, filePath);
    }

    public void addEntireSeries(String seriesName, List<? extends MediaFile> files) {
        Path seriesDir = constructSeriesPath(files.get(0).getMediaFileType(), seriesName);
        for (MediaFile file : files) {
            Path filePath = seriesDir.resolve(file.getName());
            library.addToSeries(seriesName, new LibEntry(file, filePath));
        }
    }

    @Override
    public Optional<LibraryNode> getSeriesMember(String name, int index) {
        List<LibraryNode> series = library.getSeries(name);
        if (series.size() > index) {
            return Optional.of(series.get(index));
        }
        return Optional.empty();
    }

    @Override
    public void addSingle(InputStream in, MediaFile mediaFile) {
        Path path = constructSinglePath(mediaFile);
        addSingle(new LibEntry(mediaFile, path));
        fileHandler.writeFile(in, path);
    }

    @Override
    public Optional<LibraryNode> getSingle(String fileName) {
        return library.getSingle(fileName);
    }

    /*
     * Return true if the library contains a series or single instance of this name
     */
    public boolean contains(String name) {

        return library.getSeries(name).size() > 0 || library.getSingle(name).isPresent();
    }

    /* this may need to change for a specific impl */
    public List<LibraryNode> getEntireLibrary() {
        List<LibraryNode> complete = library.collectionAsList();
        return complete;
    }

    public List<?> shuffle() {
        throw new UnsupportedOperationException("Operation currently not supported");
    }

    private WatchService initWatcher() {
        try {
            return FileSystems.getDefault().newWatchService();
        } catch (Exception err) {

        }
        return null;
    }

    private void initLibrary() {
        /*
         * Check if project root has already been initialized. If true, the library
         * needs to be loaded from the files already made available
         */
        if (fileHandler.isDirectory(rootDirectory)) {
            // traverse directory tree and add files to collection
            try {
                fileHandler.loadMedia(loader);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Consumer<List<MediaDTO>> loader = (mediaFiles) -> loadFiles(mediaFiles);

    private void createSeriesDir(Path seriesPath) {
        fileHandler.createDirectory(seriesPath);
    }

    @Override
    public Path constructSeriesPath(MediaFileType mediaType, String seriesName) {
        return rootDirectory.resolve(Path.of(mediaType.getDirName(), seriesName));
    }

    private Path constructSinglePath(MediaFile mediaFile) {
        return rootDirectory.resolve(Path.of(mediaFile.directoryName(), mediaFile.getName()));
    }

    public void initMediaTypeDirs() {
        for (MediaFileType mfileType : MediaFileType.values()) {
            fileHandler.createDirectory(rootDirectory.resolve(mfileType.getDirName()));
        }
    }

    private void commitToLog(MediaDTO dto) {
        try {
            fileHandler.writeLog(dto);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void commitSeries(String seriesName, LibraryNode entry) {
        MediaFile file = entry.file();
        MediaDTO dto = MediaDTO.newMediaDTOBuilder()
                                .contentType(file.contentType())
                                .fileName(file.getName())
                                .filePath(entry.path())
                                .mediaFileType(file.getMediaFileType())
                                .seriesName(seriesName)
                                .build();

        commitToLog(dto); 
    }

    private void commitSingle(LibraryNode entry) {
        MediaFile file = entry.file();
        MediaDTO dto = MediaDTO.newMediaDTOBuilder()
                                .contentType(file.contentType())
                                .fileName(file.getName())
                                .filePath(entry.path())
                                .mediaFileType(file.getMediaFileType())
                                .build();
        commitToLog(dto); 
    }

    final private static class LibEntry implements LibraryNode {
        private final MediaFile file;
        private final Path path;
        private final boolean commit;

        LibEntry(MediaFile file, Path path) {
            this.file = file;
            this.path = path;
            this.commit = true;
        }

        LibEntry(MediaFile file, Path path, boolean commit) {
            this.file = file;
            this.path = path;
            this.commit = commit;
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
        public boolean commit() {
            return commit;
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
        public String toString() {
            System.out.println();
            System.out.println(file == null);
            return file.contentType() + file.directoryName()  + file.getName() + " " + path.toString();
        }

        @Override
        public int hashCode() {
            int result = file.hashCode();
            return 31 * result + path.hashCode();
        }

    }


    /*
    *
    * Private use abstraction methods. 
    * Any precondition checking should be done before calling these methods
    */

    private <T extends MediaDTO> void loadFiles(List<T> loadResults) {

        for (MediaDTO dto : loadResults) {
            MediaFile mFile = null;
            try {
                mFile = MediaFile.of(dto);
            } catch (OperationNotSupportedException e) {
                // TODO Auto-generated catch block
                System.out.printf("Operation not supported for type %s, yet.", dto.mediaFileType());
                continue;
            }

            if (dto.isSeries()) {
                addToSeries(dto.seriesName(), new LibEntry(mFile, dto.filePath(), false));
            } else {
                addSingle(new LibEntry(mFile, dto.filePath(), false));
            }
        }
    }

    private void addSingle(LibraryNode entry) {
        if(entry.commit()) {
            commitSingle(entry);
        }
        library.addSingle(entry);
    }

    private void addToSeries(String seriesName, LibraryNode entry) {
        if(entry.commit()) {
            commitSeries(seriesName, entry);
        }
        library.addToSeries(seriesName, entry);
    }


}