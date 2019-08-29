package com.abledenthusiast.heracross.server.media.library.libcollection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.abledenthusiast.heracross.server.media.library.LibraryNode;
import com.abledenthusiast.heracross.server.media.library.mediafile.MediaFile;


public class MediaCollection {
    /*
    *   re-evaluate use of list v treemap for series. Usage could dictate switching.
    *   It seems unlikely that to the series would be done out of order, however this
    *   could be an incorrect assumption. Until re-eval, Just sort the list on init
    *   and mainteinance should be straightfoward from there.
    */

    private Map<String, List<LibraryNode>> seriesCollection;
    /* 
    *   repr of media that is not part of a series e.g. single movies, single images
    */
    private Map<String, LibraryNode> singles;

    public MediaCollection() {
        seriesCollection = new TreeMap<>();
        singles = new HashMap<>();
    }

    public List<LibraryNode> createSeries(String seriesName) {
        ArrayList<LibraryNode> series = new ArrayList<>();
        seriesCollection.put(seriesName, series);
        return series;
    }

    public void addSingle(LibraryNode libNode) {
        singles.put(libNode.file().getName(), libNode);
    }

    public void addToSeries(String seriesName, LibraryNode file) {
        seriesCollection.computeIfAbsent(seriesName, k -> new ArrayList<>()).add(file);
    }

    public List<LibraryNode> getSeries(String seriesName) {
        return seriesCollection.getOrDefault(seriesName, Collections.emptyList());
    }

    public Optional<LibraryNode> getSeriesItem(String name, int index) {
        if(index < 0) {
            return Optional.empty();
        }

        List<LibraryNode> series = getSeries(name);
        if(series.size() > index) {
            return Optional.of(series.get(index));
        }
        return Optional.empty();
    }

    public Optional<LibraryNode> getSingle(String fileName) {
        return Optional.of(singles.get(fileName));
    }

    /* merge the maps... there's probably a better way to do this via streams */
    public List<LibraryNode> getEntireCollection() {
        List<LibraryNode> collection = seriesCollection.values().stream().flatMap(
            list -> list.stream()
        ).collect(Collectors.toList());
        
        singles.values().forEach(
            single -> { collection.add(single); }
        );
        return collection;
    }

}