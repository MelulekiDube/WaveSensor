package wavesensor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import java.awt.Dimension;
import java.awt.HeadlessException;
import java.text.DecimalFormat;
import javafx.application.Platform;

import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;

public class TableGraphApplet extends JApplet {

    private static final int PANEL_WIDTH_INT = 600;
    private static final int PANEL_HEIGHT_INT = 400;
    private static final int TABLE_PANEL_HEIGHT_INT = 100;
    private static JFXPanel chartFxPanel;
    private static DataTableModel tableModel;
    private static JTable table;

    @Override
    public void init() {
	tableModel = Wavesensor.tableModel;
	// create javafx panel for charts
	chartFxPanel = new JFXPanel();
	chartFxPanel.setPreferredSize(new Dimension(PANEL_WIDTH_INT, PANEL_HEIGHT_INT));

	//create JTable
	table = new JTable(tableModel);
	table.setAutoCreateRowSorter(true);
	table.setGridColor(Color.DARK_GRAY);
	TableGraphApplet.DecimalFormatRenderer renderer
		= new TableGraphApplet.DecimalFormatRenderer();
	renderer.setHorizontalAlignment(JLabel.RIGHT);
	for (int i = 1; i < table.getColumnCount(); i++) {
	    table.getColumnModel().getColumn(i).setCellRenderer(renderer);
	}
	JScrollPane tablePanel = new JScrollPane(table);
	tablePanel.setPreferredSize(new Dimension(PANEL_WIDTH_INT,
		TABLE_PANEL_HEIGHT_INT));
	JPanel chartTablePanel = new JPanel();
	chartTablePanel.setLayout(new BorderLayout());

	//Create split pane that holds both the bar chart and table
	JSplitPane jsplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
	jsplitPane.setTopComponent(chartTablePanel);
	jsplitPane.setBottomComponent(tablePanel);
	jsplitPane.setDividerLocation(410);
	chartTablePanel.add(chartFxPanel, BorderLayout.CENTER);

	//Add the split pane to the content pane of the application
	add(jsplitPane, BorderLayout.CENTER);
	Platform.runLater(this::createScene);
    }

    private void createScene() {
	BarChart chart = createBarChart();
	chartFxPanel.setScene(new Scene(chart));
    }

    private BarChart createBarChart() {
	CategoryAxis xAxis = new CategoryAxis();
//	xAxis.setCategories(FXCollections.<String>observableArrayList(tableModel.
//		getColumnNames()));
	xAxis.setLabel("Individual Wave");
	double tickUnit = tableModel.getTickUnit();

	NumberAxis yAxis = new NumberAxis();
	yAxis.setTickUnit(tickUnit);
	yAxis.setLabel("Time in seconds");

	final BarChart chart = new BarChart(xAxis, yAxis, tableModel.getBarChartData());
	tableModel.addTableModelListener((TableModelEvent e) -> {

	    if (e.getType() == TableModelEvent.UPDATE) {
		final int row = e.getFirstRow();
		final int column = e.getColumn();
		final Object value
			= ((DataTableModel) e.getSource()).getValueAt(row, column);

		Platform.runLater(() -> {
		    XYChart.Series<String, Number> s
			    = (XYChart.Series<String, Number>) chart.getData().get(row);
		    BarChart.Data data = s.getData().get(column);
		    data.setYValue(value);
		});
	    }
	});
	return chart;
    }

    private static class DecimalFormatRenderer extends DefaultTableCellRenderer {

	private static final DecimalFormat FORMATTER = new DecimalFormat("#.0000");

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
	    value = FORMATTER.format((Number) value);
	    return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	}
    }
}
