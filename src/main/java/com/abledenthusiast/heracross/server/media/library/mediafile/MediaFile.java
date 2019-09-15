package com.abledenthusiast.heracross.server.media.library.mediafile;

import java.util.Objects;

import javax.naming.OperationNotSupportedException;

import com.abledenthusiast.heracross.server.fileservice.dto.MediaDTO;

public interface MediaFile {
    enum MediaFileType {
        TVSeries("TV"),
        Movie("Movie");

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

    String directoryName();


    static MediaFile createMediaFile(String name,
                                     String contentType, MediaFileType mediaType)
            throws OperationNotSupportedException {
        switch(mediaType) {
            case TVSeries: {
                return new TVMediaFile(name, contentType);
            }
            case Movie: {
                return new MovieMediaFile(name, contentType);
            }
            default: {
                throw new OperationNotSupportedException();
            }
        }
        
    }

    static MediaFile of(MediaDTO dto) throws OperationNotSupportedException {
        Objects.requireNonNull(dto);
        return createMediaFile(dto.fileName(), dto.contentType(), dto.mediaFileType());
    }
}