package org.lff.plugin.dupfinder;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;

/**
 * Created by liuff on 2017/7/8 21:52
 */
public class Plugin extends com.intellij.openapi.actionSystem.AnAction {
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        Project project = anActionEvent.getProject();
        final ProjectRootManager rootManager = ProjectRootManager.getInstance(project);
        Dialog d = new Dialog(project, rootManager);
        d.init();
        d.show();
    }
}
