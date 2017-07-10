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
import org.lff.plugin.dupfinder.vo.DuplicateClass;
import org.lff.plugin.dupfinder.vo.SourceVO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.*;
import java.util.List;

/**
 * Created by liuff on 2017/7/9 21:07
 */
public class Dialog extends DialogWrapper implements ProgressListener {

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
    private JBList myList;
    private ClassListModal listModal = new ClassListModal(new ArrayList<>());

    protected JComponent createCenterPanel() {
        final JPanel panel = new JPanel(new BorderLayout(UIUtil.DEFAULT_HGAP, UIUtil.DEFAULT_VGAP));
        myList = new JBList(listModal);

        JPanel north = new JPanel(new VerticalFlowLayout());
        bar = new JProgressBar(0, 100);
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

        List<SourceVO> dependents = new ArrayList<>();
        for (VirtualFile file : result) {
            System.out.println("Module Root " + file);
            Module module = ModuleUtil.findModuleForFile(file, project);
            ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(module);

            ModuleRootManager.getInstance(module).orderEntries().forEachLibrary(new Processor<Library>() {
                @Override
                public boolean process(Library library) {
                    String libraryName = library.getName() == null ? "<JAR>" : library.getName();
                    System.out.println("name = "+ libraryName);
                    String[] urls = library.getRootProvider().getUrls(OrderRootType.CLASSES);
                    System.out.println(Arrays.toString(urls));
                    for (String url : urls) {
                        dependents.add(new SourceVO(libraryName, url));
                    }
                    return true;
                }
            });
        }

        label.setText("Total " + dependents.size() + " found.");

        process(dependents);
    }

    private void process(List<SourceVO> dependents) {
        new Thread(()-> {
            List<DuplicateClass> clz = Finder.process(this, dependents);
            this.listModal.clear();
            clz.forEach(c -> {
                this.listModal.add(c.getFullName());
            });
        }).start();
    }

    @Override
    public void onProgess(int percent, String message) {
        SwingUtilities.invokeLater(() -> {
            this.bar.setValue(percent);
            this.label.setText(message);
        });
    }
}
