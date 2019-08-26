package com.abledenthusiast.heracross.server.media.library.mediafile;


public interface SortableMedia<T extends SortableMedia<T>> extends MediaFile, Comparable<T> {

}