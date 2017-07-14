package org.lff.plugin.dupfinder;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBTextField;
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by liuff on 2017/7/9 21:07
 */
public class Dialog extends DialogWrapper implements ProgressListener {

    private static final Logger logger = Logger.getLogger(Dialog.class.getName());


    private final Project project;
    private Finder finder;
    private ProjectRootManager rootManager = null;

    public Dialog(Project project, ProjectRootManager rootManager) {
        super(false);
        this.rootManager = rootManager;
        this.project = project;
        listModal = new ClassListModel();
        tableModel = new DuplicatesTableModel();
    }

    public void init() {
        super.init();

        setTitle("Find Duplicate classes in classpath");

        getWindow().addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
                getWindow().setCursor(new Cursor(Cursor.WAIT_CURSOR));
                javax.swing.SwingUtilities.invokeLater(() -> process());
            }

            @Override
            public void windowClosing(WindowEvent e) {

            }

            @Override
            public void windowClosed(WindowEvent e) {
                fireStop();
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
    private ClassListModel listModal;
    private DuplicatesTableModel tableModel;
    private JBTextField filter;
    private JButton btnOK;
    private JButton btnClear;
    private JBLabel filterLabel;

    protected JComponent createCenterPanel() {
        final JPanel panel = new JPanel(new BorderLayout(UIUtil.DEFAULT_HGAP, UIUtil.DEFAULT_VGAP));
        myList = new JBList(listModal);

        JPanel north = new JPanel(new VerticalFlowLayout());
        bar = new JProgressBar(0, 100);
        final JBSplitter mainPanel = new JBSplitter(true, 2f / 5);

        mainPanel.setFirstComponent(ScrollPaneFactory.createScrollPane(myList));
        mainPanel.setSecondComponent(ScrollPaneFactory.createScrollPane(new JBTable(tableModel)));


        myList.addMouseListener(new ClassMouseListener(this.listModal, this.tableModel));

        north.add(label = new JBLabel("Duplicate classes found"));
        north.add(bar);

        JPanel panelSearch = new JPanel(new HorizontalLayout(12));
        filter = new JBTextField("", 32);
        btnOK = new JButton("Filter");
        btnClear = new JButton("Clear");
        panelSearch.add(filter);
        panelSearch.add(btnOK);
        panelSearch.add(btnClear);
        filterLabel = new JBLabel("0 classes filtered.");
        panelSearch.add(filterLabel);

        btnOK.setEnabled(false);
        btnClear.setEnabled(false);

        north.add(panelSearch);

        panel.add(north, BorderLayout.NORTH);
        panel.add(mainPanel, BorderLayout.CENTER);

        panel.setPreferredSize(new Dimension(800, 600));


        btnOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                applyFilter();
            }
        });

        btnClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearFilter();
            }
        });

        return panel;
    }

    public void process() {

        java.util.List<VirtualFile> result = (rootManager.getModuleSourceRoots(ContainerUtil.set(JavaSourceRootType.SOURCE)));

        List<SourceVO> dependents = new ArrayList<>();
        for (VirtualFile file : result) {
            logger.info("Module Root " + file);
            Module module = ModuleUtil.findModuleForFile(file, project);
            ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(module);

            ModuleRootManager.getInstance(module).orderEntries().forEachLibrary(new Processor<Library>() {
                @Override
                public boolean process(Library library) {
                    String libraryName = library.getName() == null ? "<JAR>" : library.getName();
                    String[] urls = library.getRootProvider().getUrls(OrderRootType.CLASSES);
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

    private void setFinder(Finder finder) {
        this.finder = finder;
    }

    private void fireStop() {
        if (this.finder != null) {
            this.finder.stop();
        }
    }

    private void process(List<SourceVO> dependents) {
        new Thread(()-> {
            Finder finder = new Finder();
            setFinder(finder);
            List<DuplicateClass> clz = finder.process(this, dependents);
            Collections.sort(clz);
            SwingUtilities.invokeLater(() -> {
                this.listModal.clear();
                clz.forEach(c -> {
                    this.listModal.add(c.getFullName());
                    tableModel.setDependents(c.getFullName(), c.getDependents());
                });
                getWindow().setCursor(Cursor.getDefaultCursor());
                btnOK.setEnabled(true);
                btnClear.setEnabled(true);
                this.finder = null;
            });
        }).start();
    }

    @Override
    public void onProgress(int percent, String message) {
        SwingUtilities.invokeLater(() -> {
            this.bar.setValue(percent);
            this.label.setText(message);
        });
    }

    private void applyFilter() {
        String filter = this.filter.getText();
        if (filter.equals("") || filter.trim().equals("")) {
            Messages.showMessageDialog(project,
                    "Filter is empty", "Information",
                    Messages.getInformationIcon());
        }
        this.listModal.saveList();
        List<String> result = new ArrayList<>();
        for (String clz : this.listModal.getList()) {
            if (clz.contains(filter)) {
                result.add(clz);
            }
        }

        this.listModal.clear();
        this.listModal.addAll(result);

        filterLabel.setText(String.valueOf(result.size() + " classes filtered."));
    }


    private void clearFilter() {
        this.filter.setText("");
        this.listModal.clear();
        this.listModal.restore();
        filterLabel.setText("0 classes filtered.");
    }
}
