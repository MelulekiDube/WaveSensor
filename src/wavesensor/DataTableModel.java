package wavesensor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.BarChart;
import javax.swing.table.AbstractTableModel;

/**
 * SampleTableModel
 */
public class DataTableModel extends AbstractTableModel {

    private final String[] names = {"Date", "Time taken by wave"};
    private static ObservableList<BarChart.Series> bcData;
    private final List<Object[]> data;

    public DataTableModel() {
	this.data = new ArrayList<>();
	Object[] a = {"2018/10/18 14:39:16", 0.02};
	Object[] b = {"2018/10/18 14:39:16", 0.03};
	Object[] a1 = {"2018/10/18 14:39:16", 0.02};
	Object[] b1 = {"2018/10/18 14:39:16", 0.03};
	Object[] a2 = {"2018/10/18 14:39:16", 0.02};
	Object[] b2 = {"2018/10/18 14:39:16", 0.03};
	Object[] c = {"2018/10/18 14:39:16", 0.05};
	this.data.add(a);
	this.data.add(a1);
	this.data.add(a2);
	this.data.add(b);
	this.data.add(b1);
	this.data.add(b2);
	this.data.add(c);
    }

    public ObservableList<BarChart.Series> getBarChartData() {
	if (bcData == null) {
	    bcData = FXCollections.<BarChart.Series>observableArrayList();
	    for (int row = 0; row < getRowCount(); row++) {
		ObservableList<BarChart.Data> series
			= FXCollections.<BarChart.Data>observableArrayList();
		for (int column = 1; column < getColumnCount(); column++) {
		    series.add(new BarChart.Data("",
			    getValueAt(row, column)));
//		    }
		}
		bcData.add(new BarChart.Series(series));
	    }
	}
	return bcData;
    }

    public void addData(Object[] newData) {
	this.data.add(newData);
    }

    public void addData(List<Object[]> listOfData) {
	this.data.addAll(listOfData);
    }

    public double getTickUnit() {
	return 5;
    }

    public List getColumnNames() {
	return Arrays.asList(names);
    }

    @Override
    public int getRowCount() {
	return data.size();
    }

    @Override
    public int getColumnCount() {
	return names.length;
    }

    @Override
    public Object getValueAt(int row, int column) {
	return data.get(row)[column];
    }

    @Override
    public String getColumnName(int column) {
	return names[column];
    }

    @Override
    public Class getColumnClass(int column) {
	return getValueAt(0, column).getClass();
    }

    @Override
    public boolean isCellEditable(int row, int column) {
	return false;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
	if (value instanceof Double) {
	    data.get(row)[column] = (Double) value;
	}

	fireTableCellUpdated(row, column);
    }

}
