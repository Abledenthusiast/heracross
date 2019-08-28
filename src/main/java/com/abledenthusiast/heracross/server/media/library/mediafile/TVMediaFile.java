package com.abledenthusiast.heracross.server.media.library.mediafile;

import java.nio.file.Path;

//note: probably need to implement this differently. A TVMediaFile will likely be a better way to do this. 
//note: a private static class inside series may suffice.
public class TVMediaFile implements SortableMedia<TVMediaFile>   {
    private String seriesName;
    private String name;
    private String contentType;
    private Position position;
    private Path filePath;

    public final MediaFileType mediaFileType = MediaFileType.TVSeries;

    public TVMediaFile(Path rootDir, String fileName, String contentType) {
        filePath = rootDir.resolve(fileName);
        this.contentType = contentType;
    }
    
    public static TVMediaFile createMediaFile(Path rootDir, String fileName, String contentType) {
        return new TVMediaFile(rootDir, fileName, contentType);
    }

    @Override
    public Path getFilePath() {
        return filePath;
    }

    public Position getPosition() {
        return position;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof TVMediaFile)) {
            return false;
        }
        TVMediaFile mf = (TVMediaFile) other;

        //FIXME
        return mf.name.equalsIgnoreCase(name);
    }

    @Override
    public int hashCode() {
        int result = seriesName.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + contentType.hashCode();
        result = 31 * result + position.hashCode();
        return 31 * result + filePath.hashCode();
    }

    @Override
    public int compareTo(TVMediaFile o) {
        int result = Integer.compare(position.season, o.getPosition().season);
        if (result == 0) {
            result =  Integer.compare(position.episode, o.getPosition().episode);
        }
        return result;
    }
    /*
    * defines the position of the file in the series
    */
    private static class Position {
        private int episode;
        private int season;

        @Override
        public int hashCode() {
            int result = Integer.hashCode(episode);
            return 31 * result + Integer.hashCode(season);
        }
    }

    /* return the name of this epsidoe. */
	public String getName() {
		return name;
    }

    @Override
    public MediaFileType getMediaFileType() {
        return mediaFileType;
    }

    public String getContentType() {
        return contentType;
    }

}