package com.abledenthusiast.heracross.server.fileservice;


public interface FileHandler<T> {

    boolean writeFile(T file);

    boolean createDirectory(String path);

}