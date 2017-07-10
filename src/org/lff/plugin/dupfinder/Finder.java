package org.lff.plugin.dupfinder;

import org.lff.plugin.dupfinder.utility.FilenameUtility;
import org.lff.plugin.dupfinder.vo.DuplicateClass;

import java.io.IOException;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;

/**
 * @author Feifei Liu
 * @datetime Jul 10 2017 10:16
 */
public class Finder {

    private static final Logger logger = Logger.getLogger(Finder.class.getName());

    public static List<DuplicateClass> process(ProgressListener listener, Set<String> dependents) {
        Map<String, HashSet<String>> map = new HashMap<>();
        int totalSize = dependents.size() + 2;
        int count = 0;
        for (String name : dependents) {
            count++;
            listener.onProgess((int)( 100 * count / (float)(totalSize)), "Processing " + name);
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
        listener.onProgess((int)((totalSize - 1) / (float)(totalSize)), "Calculating... ");
        List<DuplicateClass> result = findDuplicates(map);
        listener.onProgess(100, "Finished.");
        logger.info("--- Duplicates " + result.size() + " START ---");
        for (DuplicateClass clz : result) {
            logger.info("        " + clz.getFullName());
        }
        logger.info("--- Duplicates END ---  ");
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
                if (e.isDirectory()) {
                    continue;
                }
                String entryName = e.getName();
                if (entryName.startsWith("META-INF/")) {
                    continue;
                }
                if (!entryName.endsWith(".class")) {
                    continue;
                }
                result.add(entryName);
            }
        } catch (IOException e) {
        }
        return result;
    }
}
