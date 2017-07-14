package org.lff.plugin.dupfinder;

import org.lff.plugin.dupfinder.utility.FilenameUtility;
import org.lff.plugin.dupfinder.vo.DuplicateClass;
import org.lff.plugin.dupfinder.vo.SourceVO;

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

    private volatile boolean stopped = false;


    private static final Logger logger = Logger.getLogger(Finder.class.getName());

    public List<DuplicateClass> process(ProgressListener listener, List<SourceVO> dependents) {
        Map<String, HashSet<SourceVO>> map = new HashMap<>();
        int totalSize = dependents.size() + 2;
        int count = 0;
        for (SourceVO vo : dependents) {
            if (stopped) {
                return new ArrayList<>();
            }

            String name = vo.getUrl();
            String library = vo.getLibrary();
            count++;
            listener.onProgress((int)( 100 * count / (float)(totalSize)), "Processing " + name);
            if (name == null) {
                continue;
            }

            if (name.startsWith("jar://")) {
                List<String> classes = loadClassNames(name);
                for (String clz : classes) {
                    addClass(map, clz, vo);
                }
            }
        }
        listener.onProgress((int)(100 * (totalSize - 1) / (float)(totalSize)), "Calculating... ");
        List<DuplicateClass> result = findDuplicates(map);
        listener.onProgress(100, "Finished. " + result.size() + " duplicate class found in " + dependents.size() +
                        " files");
        logger.info("--- Duplicates " + result.size() + " START ---");
        for (DuplicateClass clz : result) {
            logger.info("        " + clz.getFullName());
        }
        logger.info("--- Duplicates END ---  ");
        return result;
    }

    private List<DuplicateClass> findDuplicates(Map<String, HashSet<SourceVO>> map) {
        List<DuplicateClass> result = new ArrayList<>();
        for (String clz : map.keySet()) {
            if (stopped) {
                return new ArrayList<>();
            }
            HashSet<SourceVO> dependents = map.get(clz);
            if (dependents != null && dependents.size() > 1) {
                result.add(new DuplicateClass(clz, dependents));
            }
        }
        return result;
    }

    private  void addClass(Map<String, HashSet<SourceVO>> map, String clz, SourceVO vo) {
        HashSet<SourceVO> dependents = map.get(clz);
        if (dependents == null) {
            dependents = new HashSet<>();
            dependents.add(vo);
            map.put(clz, dependents);
        } else {
            dependents.add(vo);
        }
    }

    private List<String> loadClassNames(String name) {
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
                logger.info("Adding " + entryName);
                result.add(entryName);
                if (stopped) {
                    return result;
                }
            }
        } catch (IOException e) {
        }
        return result;
    }

    public void stop() {
        logger.info("STOP called");
        this.stopped = true;
    }
}
