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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SourceVO sourceVO = (SourceVO) o;

        if (module != null ? !module.equals(sourceVO.module) : sourceVO.module != null) return false;
        if (library != null ? !library.equals(sourceVO.library) : sourceVO.library != null) return false;
        return url != null ? url.equals(sourceVO.url) : sourceVO.url == null;
    }

    @Override
    public int hashCode() {
        int result = module != null ? module.hashCode() : 0;
        result = 31 * result + (library != null ? library.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        return result;
    }
}
