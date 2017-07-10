package org.lff.plugin.dupfinder;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Feifei Liu
 * @datetime Jul 10 2017 11:54
 */
public class ClassListModel extends AbstractListModel<String> {
    private static final long serialVersionUID = 7214161645270908310L;
    private final List<String> list = new ArrayList<>();
    private List<String> savedList = new ArrayList<>();

    public ClassListModel() {
    }

    public List<String> getList() {
        return this.list;
    }

    @Override
    public int getSize() {
        return list.size();
    }

    @Override
    public String getElementAt(int index) {
        return list.get(index);
    }

    public void clear() {
        int index1 = list.size()-1;
        list.clear();
        if (index1 >= 0) {
            fireIntervalRemoved(this, 0, index1);
        }
    }

    public void add(String t) {
        list.add(t);
        int index0 = list.size() - 1;
        int index1 = index0;
        fireIntervalAdded(this, index0, index1);
    }

    public void saveList() {
        this.savedList = new ArrayList<>();
        this.savedList.addAll(list);

    }

    public void addAll(List<String> result) {
        int size = list.size();
        this.list.addAll(result);
        fireIntervalAdded(this, size, list.size() - 1);
    }
}
