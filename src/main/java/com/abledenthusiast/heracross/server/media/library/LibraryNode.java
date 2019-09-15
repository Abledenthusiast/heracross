package com.abledenthusiast.heracross.server.media.library;

import com.abledenthusiast.heracross.server.media.library.mediafile.MediaFile;

import java.nio.file.Path;

public interface LibraryNode {
    MediaFile file();
    Path path();
    @Override
    boolean equals(Object var1);
    boolean commit();
}
