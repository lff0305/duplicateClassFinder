package org.lff.plugin.dupfinder.vo;

/**
 * @author Feifei Liu
 * @datetime Jul 10 2017 11:41
 */
public class SourceVO {
    public String getModule() {
        return module;
    }

    private final String module;

    public SourceVO(String module, String library, String url) {
        this.library = library;
        this.url = url;
        this.module = module;
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
