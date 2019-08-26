package com.abledenthusiast.heracross.server.media.library.libcollection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.abledenthusiast.heracross.server.media.library.mediafile.MediaFile;


public class MediaCollection {
    /*
    *   re-evaluate use of list v treemap for series. Usage could dictate switching.
    *   It seems unlikely that to the series would be done out of order, however this
    *   could be an incorrect assumption. Until re-eval, Just sort the list on init
    *   and mainteinance should be straightfoward from there.
    */

    private Map<String, List<MediaFile>> seriesCollection;
    /* 
    *   repr of media that is not part of a series e.g. single movies, single images
    */
    private Map<String, MediaFile> singles;

    public MediaCollection() {
        seriesCollection = new TreeMap<>();
        singles = new HashMap<>();
    }

    public List<MediaFile> createSeries(String seriesName) {
        ArrayList<MediaFile> series = new ArrayList<>();
        seriesCollection.put(seriesName, series);
        return series;
    }

    public void addSingle(MediaFile file) {
        singles.put(file.getName(), file);
    }

    public void addToSeries(String seriesName, MediaFile file) {
        seriesCollection.computeIfAbsent(seriesName, k -> new ArrayList<>()).add(file);
    }

    public List<MediaFile> getSeries(String seriesName) {
        return seriesCollection.getOrDefault(seriesName, Collections.emptyList());
    }

    public Optional<MediaFile> getSeriesItem(String name, int index) {
        List<MediaFile> series = getSeries(name);
        if(series.size() > index) {
            return Optional.of(series.get(index));
        }
        return Optional.empty();
    }

    public Optional<MediaFile> getSingle(String fileName) {
        return Optional.of(singles.get(fileName));
    }

    /* merge the maps... there's probably a better way to do this via streams */
    public List<MediaFile> getEntireCollection() {
        List<MediaFile> collection = seriesCollection.values().stream().flatMap(
            list -> list.stream()
        ).collect(Collectors.toList());
        
        singles.values().forEach(
            single -> { collection.add(single); }
        );
        return collection;
    }

	public void put(String name, MediaFile media) {
	}
}