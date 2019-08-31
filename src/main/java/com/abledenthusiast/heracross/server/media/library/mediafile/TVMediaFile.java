package com.abledenthusiast.heracross.server.media.library.mediafile;

import java.nio.file.Path;

//note: probably need to implement this differently. A TVMediaFile will likely be a better way to do this. 
//note: a private static class inside series may suffice.
public class TVMediaFile implements SortableMedia<TVMediaFile>   {
    private String name;
    private String contentType;
    private Position position;

    public final MediaFileType mediaFileType = MediaFileType.TVSeries;

    public TVMediaFile(String fileName, String contentType)
    {
        this.name = fileName;
        this.contentType = contentType;
    }
    
    public static TVMediaFile createMediaFile(String fileName, String contentType) {
        return new TVMediaFile(fileName, contentType);
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
        int result = name.hashCode();
        result = 31 * result + contentType.hashCode();
        return 31 * result + position.hashCode();
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

    @Override
    public String contentType() {
        return contentType;
    }

}