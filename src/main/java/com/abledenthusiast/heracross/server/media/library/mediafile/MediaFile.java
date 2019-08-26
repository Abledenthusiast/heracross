package com.abledenthusiast.heracross.server.media.library.mediafile;

import java.nio.file.Path;

import javax.naming.OperationNotSupportedException;

public interface MediaFile {
    public enum MediaFileType {
        TVSeries,
        Movie,
    }

    public String getName();

    public int hashCode();

    public Path getFilePath();

    public static MediaFile createMediaFile(Path rootDir, String name,
                                             String contentType, MediaFileType mediaType)
            throws OperationNotSupportedException {
        switch(mediaType) {
            case TVSeries: {
                return new TVMediaFile(rootDir, name, contentType);
            }
            case Movie: {
                throw new OperationNotSupportedException();
            }
            default: {
                throw new OperationNotSupportedException();
            }
        }
        
    }
}