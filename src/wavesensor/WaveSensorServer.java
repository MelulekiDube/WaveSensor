/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wavesensor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dube_
 */
public class WaveSensorServer {

    private final ServerSocket socket; //this is the welcoming socket
    private Socket clientSocket; // this is the client socket which is the pi
    private final int PORT; // the port that this be at
    PrintWriter writer;

    public WaveSensorServer() throws IOException {
	PORT = 5555;
	socket = new ServerSocket(PORT);
    }

    public void sendMessage(String cmd) {
	writer.write(cmd + " start");
	writer.flush();
    }

    public void sendMessage(String cmd, int sv) {
	writer.write(cmd + " changeBase " + sv);
	writer.flush();
    }

    public void turnSensorOff() {
	writer.write(Constants.TAG_POWER + " stop");
	writer.flush();
    }

    public void reconect() {
	try {
	    clientSocket.close();
	} catch (IOException ex) {
	    Logger.getLogger(WaveSensorServer.class.getName()).log(Level.SEVERE, null, ex);
	}
	this.connect();
    }

    /**
     * This waits for a client and then immediately starts a new thread which
     * will be waiting for data from client
     *
     * s
     */
    final public void connect() {
	try {
	    clientSocket = socket.accept();
	    writer = new PrintWriter(new DataOutputStream(clientSocket.getOutputStream()));
	    Thread thread = new Thread(new ReceiveClientData(new DataInputStream(clientSocket.getInputStream()), this));
	    thread.start();
	    sendMessage("start");
	} catch (IOException ex) {
	    Logger.getLogger(WaveSensorServer.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    void disconnect() {
	try {
	    this.clientSocket.close();
	} catch (IOException ex) {
	    Logger.getLogger(WaveSensorServer.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

}
