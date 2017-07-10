package org.lff.plugin.dupfinder.vo;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Feifei Liu
 * @datetime Jul 10 2017 10:17
 */
public class DuplicateClass {


    public DuplicateClass(String fullName, Set<String> dependents) {
        this.fullName = fullName;
        this.dependents = dependents;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Set<String> getDependents() {
        return dependents;
    }

    public void setDependents(Set<String> dependents) {
        this.dependents = dependents;
    }

    private String fullName;

    private Set<String> dependents;
}