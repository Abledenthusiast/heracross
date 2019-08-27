package com.abledenthusiast.heracross.server.fileservice;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

public interface FileHandler<T> {

    public boolean writeFile(T file);

    public boolean createDirectory(Path directory);

    public boolean isDirectory(Path directory);

    public File getFile(Path directory);

    public File[] getFiles(Path directory);

    public void writeLog(String logstr) throws IOException;

    public Set<String> loadLog() throws IOException;

}