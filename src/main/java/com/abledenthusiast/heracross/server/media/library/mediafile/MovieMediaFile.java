package com.abledenthusiast.heracross.server.media.library.mediafile;

public class MovieMediaFile implements SortableMedia<MovieMediaFile> {
    public final MediaFileType mediaFileType = MediaFileType.Movie;

    private String fileName;
    private String contentType;

    public MovieMediaFile(String fileName, String contentType) {
        this.fileName = fileName;
        this.contentType = contentType;
    }

    public static MovieMediaFile createMediaFile(String fileName, String contentType) {
        return new MovieMediaFile(fileName, contentType);
    }

    @Override
    public String getName() {
        return fileName;
    }

    @Override
    public MediaFileType getMediaFileType() {
        return mediaFileType;
    }

    @Override
    public String contentType() {
        return contentType;
    }

    @Override
    public int compareTo(MovieMediaFile o) {
        int result = o.fileName.compareTo(fileName);
        if (result == 0) {
            result = o.contentType.compareTo(contentType);
        }
        return result;
    }

    @Override
    public int hashCode() {
        int result = fileName.hashCode();
        return 31 * result + contentType.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof MovieMediaFile)) {
            return false;
        }
        MovieMediaFile mf = (MovieMediaFile) other;

        //FIXME
        return mf.fileName.equalsIgnoreCase(fileName) && mf.contentType.equals(contentType);
    }

    @Override
    public String directoryName() {
        // TODO Auto-generated method stub
        return mediaFileType.getDirName();
    }

}