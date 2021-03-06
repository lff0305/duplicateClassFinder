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

    public List<DuplicateClass> process(ProgressListener listener, List<SourceVO> dependents, boolean allowSameClassInDifferentModulesSelected) {
        Map<String, TreeSet<SourceVO>> map = new HashMap<>();
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
        List<DuplicateClass> result = findDuplicates(map, allowSameClassInDifferentModulesSelected);
        listener.onProgress(100, "Finished. " + result.size() + " duplicate class found in " + dependents.size() +
                        " files");
        return result;
    }

    private List<DuplicateClass> findDuplicates(Map<String, TreeSet<SourceVO>> map, boolean allowSameClassInDifferentModules) {
        List<DuplicateClass> result = new ArrayList<>();
        for (String clz : map.keySet()) {
            if (stopped) {
                return new ArrayList<>();
            }
            TreeSet<SourceVO> dependents = map.get(clz);
            if (!allowSameClassInDifferentModules) {
                if (dependents != null && dependents.size() > 1) {
                    result.add(new DuplicateClass(clz, dependents));
                }
            } else {
                Map<String, Integer> moduleMap = new HashMap<>();
                for (SourceVO vo : dependents) {
                    String module = vo.getModule();
                    Integer count = moduleMap.get(module);
                    if (count == null) {
                        count = 0;
                    }
                    count++;
                    moduleMap.put(module, count);
                }
                for (String module : moduleMap.keySet()) {
                    int count = moduleMap.get(module);
                    if (count > 1) {
                        result.add(new DuplicateClass(clz, dependents));
                        break;
                    }
                }
                moduleMap = null;
            }
        }
        return result;
    }

    private  void addClass(Map<String, TreeSet<SourceVO>> map, String clz, SourceVO vo) {
        TreeSet<SourceVO> dependents = map.get(clz);
        if (dependents == null) {
            dependents = new TreeSet<>();
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
