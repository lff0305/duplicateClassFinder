package org.lff.plugin.dupfinder;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.Processor;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.jps.model.java.JavaSourceRootType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by liuff on 2017/7/8 21:52
 */
public class Plugin extends com.intellij.openapi.actionSystem.AnAction {
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        Project project = anActionEvent.getProject();
        final ProjectRootManager rootManager = ProjectRootManager.getInstance(project);
        List<VirtualFile> result = (rootManager.getModuleSourceRoots(ContainerUtil.set(JavaSourceRootType.SOURCE)));

        for (VirtualFile file : result) {
            System.out.println("Module Root " + file);
            Module module = ModuleUtil.findModuleForFile(file, project);
            ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(module);

            ModuleRootManager.getInstance(module).orderEntries().forEachLibrary(new Processor<Library>() {
                @Override
                public boolean process(Library library) {
                    System.out.println("name = "+ library.getName() + " ");
                    String[] urls = library.getRootProvider().getUrls(OrderRootType.CLASSES);
                    System.out.println(Arrays.toString(urls));
                    return true;
                }
            });
        }

        Dialog d = new Dialog();
        d.init();
        d.show();
    }
}
