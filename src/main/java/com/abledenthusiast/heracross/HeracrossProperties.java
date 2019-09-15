package com.abledenthusiast.heracross;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("heracross")
public class HeracrossProperties {
 
    private String version;

    private String directoryStore;

    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }

    public String getDirectoryStore() {
        return directoryStore;
    }

    public void setDirectoryStore(String directoryStore) {
        this.directoryStore = directoryStore;
    }
 
}
