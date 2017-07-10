package org.lff.plugin.dupfinder.vo;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * @author Feifei Liu
 * @datetime Jul 10 2017 10:17
 */
public class DuplicateClass implements Comparable<DuplicateClass> {


    public DuplicateClass(String fullName, Set<SourceVO> dependents) {
        this.fullName = fullName;
        this.dependents = dependents;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Set<SourceVO> getDependents() {
        return dependents;
    }

    public void setDependents(Set<SourceVO> dependents) {
        this.dependents = dependents;
    }

    private String fullName;

    private Set<SourceVO> dependents;

    @Override
    public int compareTo(@NotNull DuplicateClass o) {
        return fullName.compareTo(o.getFullName());
    }
}
