/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wavesensor;

import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.List;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author dube_
 */
public class Wavesensor {

    public static WaveSensorServer server;
    public static DataTableModel tableModel;
    public static Controller controller;
    public static JFrame frame;
    public static JApplet applet;

    public static void initServer() throws IOException {
	server = new WaveSensorServer();
	server.connect();
    }

    public static void addDataToTable(Object[] data) {
	if (controller != null) {
	    controller.addToTable(data);
	}
    }

    public static void addListToTable(List<Object[]> data) {
	tableModel.addData(data);
	tableModel.fireTableDataChanged();
    }

    public static void initModel() {
	tableModel = new DataTableModel();
    }

    static void sendCommand(String TAG_POWER) {
	server.sendMessage(TAG_POWER);
    }

    static void turnSensorOff() {
	server.turnSensorOff();
    }

    static void adjustSensitivity(int senseValue) {
	server.sendMessage(Constants.TAG_SENSITIVITY, senseValue);
    }

    static void loggedIn() {
	controller = new Controller();
	controller.setVisible(true);
    }

    static void initData() {
	frame = new JFrame("Wave Sensor Data");
	frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	//create appelt with the daya and graphs
	applet = new TableGraphApplet();
	frame.addWindowListener(new WindowAdapter() {
	    @Override
	    public void windowClosing(WindowEvent e) {
		controller.toFront();
	    }
	}
	);
	applet.init();
	frame.setContentPane(applet.getContentPane());
	frame.pack();
	frame.setLocationRelativeTo(null);
	frame.setResizable(true);
    }

    static void viewController() {
	controller.setVisible(true);
    }

    static void viewData() {
	SwingUtilities.invokeLater(() -> {
	    try {
		UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
	    } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
	    }
	    frame.setVisible(true);
	    applet.start();
	});

    }

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
	// TODO code application logic here
	initModel();
	initServer();
	initData();
	EventQueue.invokeLater(() -> {
	    new Login().setVisible(true);
	});
    }

}
