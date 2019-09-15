package com.abledenthusiast.heracross.server.fileservice;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

import javax.print.attribute.standard.Media;

import com.abledenthusiast.heracross.server.fileservice.dto.MediaDTO;
import com.abledenthusiast.heracross.server.media.library.mediafile.MediaFile;

public interface FileHandler {

    boolean writeFile(InputStream in, Path targetLocation);

    boolean createDirectory(Path directory);

    boolean isDirectory(Path directory);

    File getFile(Path directory);

    File[] getFiles(Path directory);

    void writeLog(MediaDTO dto) throws Exception;

    List<MediaDTO> loadMedia() throws Exception;

    void loadMedia(Consumer<List<MediaDTO>> loader) throws Exception;

}