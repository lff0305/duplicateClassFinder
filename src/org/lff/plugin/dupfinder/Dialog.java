package org.lff.plugin.dupfinder;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBList;
import com.intellij.ui.table.JBTable;
import com.intellij.util.Processor;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.ui.UIUtil;
import org.jdesktop.swingx.HorizontalLayout;
import org.jetbrains.jps.model.java.JavaSourceRootType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.*;

/**
 * Created by liuff on 2017/7/9 21:07
 */
public class Dialog extends DialogWrapper {

    private final Project project;
    private ProjectRootManager rootManager = null;

    public Dialog(Project project, ProjectRootManager rootManager) {
        super(false);
        this.rootManager = rootManager;
        this.project = project;
    }

    public void init() {
        super.init();
        setTitle("Find Duplicate classes in classpath");

        getWindow().addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
                javax.swing.SwingUtilities.invokeLater(() -> process());
            }

            @Override
            public void windowClosing(WindowEvent e) {

            }

            @Override
            public void windowClosed(WindowEvent e) {

            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });

    }

    protected Action[] createActions() {
        return new Action[]{this.getOKAction()};
    }


    private JProgressBar bar;
    private JBLabel label;

    protected JComponent createCenterPanel() {
        final JPanel panel = new JPanel(new BorderLayout(UIUtil.DEFAULT_HGAP, UIUtil.DEFAULT_VGAP));
        JBList myList = new JBList();

        JPanel north = new JPanel(new VerticalFlowLayout());
        bar = new JProgressBar();
        final JBSplitter mainPanel = new JBSplitter(true, 1f / 3);

        mainPanel.setFirstComponent(ScrollPaneFactory.createScrollPane(myList));
        mainPanel.setSecondComponent(ScrollPaneFactory.createScrollPane(new JBTable()));


        north.add(label = new JBLabel("Duplicate classes found"));
        north.add(bar);

        panel.add(north, BorderLayout.NORTH);
        panel.add(mainPanel, BorderLayout.CENTER);

        panel.setPreferredSize(new Dimension(800, 400));

        return panel;
    }

    public void process() {

        java.util.List<VirtualFile> result = (rootManager.getModuleSourceRoots(ContainerUtil.set(JavaSourceRootType.SOURCE)));

        Set<String> dependents = new HashSet<>();
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
                    dependents.addAll(Arrays.asList(urls));
                    return true;
                }
            });
        }

        label.setText("Total " + dependents.size() + " found.");

        process(dependents);
    }

    private void process(Set<String> dependents) {
        Finder.process(dependents);
    }
}
