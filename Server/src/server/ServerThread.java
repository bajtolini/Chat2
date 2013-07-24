package server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ServerThread extends Thread {

    private boolean stop;
    private Server server = null;
    private Socket socket = null;
    private int ID = 1666;
    private DataInputStream streamIn = null;
    private DataOutputStream streamOut = null;

    public ServerThread(Server _server, Socket _socket) {
        super();
        this.stop = false;
        server = _server;
        socket = _socket;
        ID = socket.getPort();
    }

    public void send(String msg) {
        try {
            streamOut.writeUTF(msg);
            streamOut.flush();
        } catch (IOException ioe) {
            System.err.println(ID + " ERROR sending: " + ioe.getMessage());
            server.remove(ID);
        }
    }

    public int getID() {
        return ID;
    }

    public void run() {
        System.out.println("Server Thread " + ID + " running.");
        while (!this.stop) {
            try {
                server.handle(ID, streamIn.readUTF());
            } catch (IOException ioe) {
                System.err.println(ID + " ERROR reading: " + ioe.getMessage());
                server.remove(ID);
                break;
            }
        }
    }

    public void open() throws IOException {
        streamIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        streamOut = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
    }

    public void close() throws IOException {
        if (socket != null) {
            socket.close();
        }
        if (streamIn != null) {
            streamIn.close();
        }
        if (streamOut != null) {
            streamOut.close();
        }
    }
    
    public void finish() {
        this.stop = true;
    }
}