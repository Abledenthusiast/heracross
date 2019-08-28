package com.abledenthusiast.heracross.server.fileservice;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

public interface FileHandler<T> {

    boolean writeFile(T file);

    boolean createDirectory(Path directory);

    boolean isDirectory(Path directory);

    File getFile(Path directory);

    File[] getFiles(Path directory);

    void writeLog(String logstr) throws IOException;

    Set<String> loadLog() throws IOException;

}