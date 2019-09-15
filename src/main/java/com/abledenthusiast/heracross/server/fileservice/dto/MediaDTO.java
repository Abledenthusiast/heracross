package com.abledenthusiast.heracross.server.fileservice.dto;
//to be refactored

import java.nio.file.Path;

import com.abledenthusiast.heracross.server.media.library.mediafile.MediaFile.MediaFileType;

public class MediaDTO {
    private Path filePath;

    private String fileName;

    private MediaFileType mediaFileType;

    private String contentType;

    private String seriesName;

    public MediaDTO(MediaDTOBuilder builder) {
      this.filePath = builder.filePath;
      this.fileName = builder.fileName;
      this.mediaFileType = builder.mediaFileType;
      this.contentType = builder.contentType;
      this.seriesName = builder.seriesName;
    }

    public String seriesName() {
        return seriesName;
    }

    public Path filePath() {
        return filePath;
    }

    public String fileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public MediaFileType mediaFileType() {
        return mediaFileType;
    }

    public void setMediaFileType(MediaFileType mediaFileType) {
        this.mediaFileType = mediaFileType;
    }

    public String contentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
    }

    public boolean isSeries() {
        return seriesName != null && !seriesName.equals("");
    }

    public static MediaDTOBuilder newMediaDTOBuilder() {
      return new MediaDTOBuilder();
    }


    public final static class MediaDTOBuilder {
      private Path filePath;
      private String fileName;
      private MediaFileType mediaFileType;
      private String contentType;
      private String seriesName;

      public MediaDTOBuilder filePath(Path filePath) {
        this.filePath = filePath;
        return this;
      }

      public MediaDTOBuilder fileName(String fileName) {
        this.fileName = fileName;
        return this;
      }

      public MediaDTOBuilder mediaFileType(MediaFileType mediaFileType) {
        this.mediaFileType = mediaFileType;
        return this;
      }

      public MediaDTOBuilder contentType(String contentType) {
        this.contentType = contentType;
        return this;
      }

      public MediaDTOBuilder seriesName(String seriesName) {
        this.seriesName = seriesName;
        return this;
      }

      public MediaDTO build() {
        return new MediaDTO(this);
      }

    }
}