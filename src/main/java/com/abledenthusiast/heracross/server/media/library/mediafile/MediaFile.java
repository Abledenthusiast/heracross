package com.abledenthusiast.heracross.server.media.library.mediafile;

import java.nio.file.Path;

import javax.naming.OperationNotSupportedException;

public interface MediaFile {
    enum MediaFileType {
        TVSeries("TV"),
        Movie("Movie"),
        ;

        private final String dirName;

        MediaFileType(String dirName) {
            this.dirName = dirName;
        }

        public String getDirName() {
            return dirName;
        }
    }

    String getName();

    MediaFileType getMediaFileType();

    String contentType();

    @Override int hashCode();

    @Override String toString();


    static MediaFile createMediaFile(String name,
                                     String contentType, MediaFileType mediaType)
            throws OperationNotSupportedException {
        switch(mediaType) {
            case TVSeries: {
                return new TVMediaFile(name, contentType);
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