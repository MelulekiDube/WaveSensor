/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wavesensor;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dube_
 */
public class ReceiveClientData implements Runnable {

    BufferedReader reader;
    WaveSensorServer server;

    public ReceiveClientData(DataInputStream input, WaveSensorServer serv) {
	reader = new BufferedReader(new InputStreamReader(input));
	this.server = serv;
    }

    @Override
    public void run() {
	while (true) {
	    String data;
	    try {
		/*if ((*/
		data = reader.readLine();/*) != null) */
		System.out.println(data);
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		Object[] o = {dtf.format(now), Double.parseDouble(data)};
		Wavesensor.addDataToTable(o);
	    } catch (IOException | NumberFormatException ex) {
		server.reconect();
	    }
	}
    }

}
