package org.lff.plugin.dupfinder;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBList;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.UIUtil;
import org.jdesktop.swingx.HorizontalLayout;

import javax.swing.*;
import java.awt.*;

/**
 * Created by liuff on 2017/7/9 21:07
 */
public class Dialog extends DialogWrapper {
    public Dialog() {
        super(false);
    }

    public void init() {
        super.init();

        setTitle("Find Duplicate classes in classpath");
    }

    protected Action[] createActions() {
        return new Action[]{this.getOKAction()};
    }

    protected JComponent createCenterPanel() {
        final JPanel panel = new JPanel(new BorderLayout(UIUtil.DEFAULT_HGAP, UIUtil.DEFAULT_VGAP));
        JBList myList = new JBList();

        JPanel north = new JPanel(new VerticalFlowLayout());
        JProgressBar bar = new JProgressBar();
        final JBSplitter mainPanel = new JBSplitter(true, 1f / 3);

        mainPanel.setFirstComponent(ScrollPaneFactory.createScrollPane(myList));
        mainPanel.setSecondComponent(ScrollPaneFactory.createScrollPane(new JBTable()));


        north.add(new JBLabel("Duplicate classes found"));
        north.add(bar);

        panel.add(north, BorderLayout.NORTH);
        panel.add(mainPanel, BorderLayout.CENTER);

        panel.setPreferredSize(new Dimension(800, 400));

        return panel;
    }
}
