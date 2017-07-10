package org.lff.plugin.dupfinder;

import org.lff.plugin.dupfinder.utility.FilenameUtility;
import org.lff.plugin.dupfinder.vo.DuplicateClass;

import java.io.IOException;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author Feifei Liu
 * @datetime Jul 10 2017 10:16
 */
public class Finder {
    public static List<DuplicateClass> process(Set<String> dependents) {
        Map<String, HashSet<String>> map = new HashMap<>();
        for (String name : dependents) {
            if (name == null) {
                continue;
            }
            if (name.startsWith("jar://")) {
                List<String> classes = loadClassNames(name);
                for (String clz : classes) {
                    addClass(map, clz, name);
                }
            }
        }
        List<DuplicateClass> result = findDuplicates(map);
        return result;
    }

    private static List<DuplicateClass> findDuplicates(Map<String, HashSet<String>> map) {
        List<DuplicateClass> result = new ArrayList<>();
        for (String clz : map.keySet()) {
            HashSet<String> dependents = map.get(clz);
            if (dependents != null && dependents.size() > 1) {
                result.add(new DuplicateClass(clz, dependents));
            }
        }
        return result;
    }

    private static void addClass(Map<String, HashSet<String>> map, String clz, String name) {
        HashSet<String> dependents = map.get(clz);
        if (dependents == null) {
            dependents = new HashSet<>();
            dependents.add(name);
            map.put(clz, dependents);
        } else {
            dependents.add(name);
        }
    }

    private static List<String> loadClassNames(String name) {
        List<String> result = new ArrayList<>();
        String fileName = FilenameUtility.getFileName(name);
        try {
            JarFile file = new JarFile(fileName);
            Enumeration<JarEntry> entries = file.entries();
            while (entries.hasMoreElements()) {
                JarEntry e = entries.nextElement();
                result.add(e.getName());
            }
        } catch (IOException e) {
        }
        return result;
    }
}
