package org.lff.plugin.dupfinder.vo;

/**
 * @author Feifei Liu
 * @datetime Jul 10 2017 11:41
 */
public class SourceVO {
    public SourceVO(String library, String url) {
        this.library = library;
        this.url = url;
    }

    public String getLibrary() {

        return library;
    }

    public String getUrl() {
        return url;
    }

    private String library;
    private String url;
}
