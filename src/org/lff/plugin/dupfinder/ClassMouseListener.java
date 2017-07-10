package org.lff.plugin.dupfinder;

import com.intellij.ui.components.JBList;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * @author Feifei Liu
 * @datetime Jul 10 2017 15:12
 */
public class ClassMouseListener implements MouseListener {

    private final DuplicatesTableModel tableModel;
    private ClassListModel model;

    public ClassMouseListener(ClassListModel model, DuplicatesTableModel tableModel) {
        this.model = model;
        this.tableModel = tableModel;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        JBList list = (JBList)e.getSource();
        if (e.getClickCount() == 1) {
            int index = list.locationToIndex(e.getPoint());
            String fullname = model.getElementAt(index);
            tableModel.clear();
            tableModel.show(fullname);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}

