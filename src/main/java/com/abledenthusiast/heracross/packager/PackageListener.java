package com.abledenthusiast.heracross.packager;


public class PackageListener {
    private boolean unprocessed;
    
    public PackageListener() {
        unprocessed = false;
    }

    public void wake() {
        unprocessed = true;
    }
}