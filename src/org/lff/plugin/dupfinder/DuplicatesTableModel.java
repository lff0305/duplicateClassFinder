package org.lff.plugin.dupfinder;

import org.lff.plugin.dupfinder.vo.SourceVO;

import javax.swing.table.AbstractTableModel;
import java.util.*;

/**
 * @author Feifei Liu
 * @datetime Jul 10 2017 13:54
 */
public class DuplicatesTableModel extends AbstractTableModel {

    static final String[] NAMES = new String[]{"Type", "Library"};
    private static final long serialVersionUID = 2834765790738917135L;

    private List<Map<Integer, String>> data = new LinkedList<>();

    private Map<String, Set<SourceVO>> duplicates = new HashMap<>();

    @Override
    public String getColumnName(int column) {
        return NAMES[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Map<Integer, String> rowData = data.get(rowIndex);
        return rowData.get(columnIndex);
    }

    public void add(SourceVO vo) {
        Map<Integer, String> rowData = new HashMap<>();
        rowData.put(0, vo.getLibrary());
        rowData.put(1, vo.getUrl());
        data.add(rowData);
        int index0 = data.size() - 1;
        int index1 = index0;
        this.fireTableRowsInserted(index0, index1);
    }

    public void setDependents(String fullName, Set<SourceVO> dependents) {
        duplicates.put(fullName, dependents);
    }

    public void clear() {
        int index1 = data.size()-1;
        data.clear();
        if (index1 >= 0) {
            fireTableRowsDeleted(0, index1);
        }
    }

    public void show(String fullname) {
        Set<SourceVO> list = duplicates.get(fullname);
        for (SourceVO vo : list) {
            this.add(vo);
        }
    }
}
