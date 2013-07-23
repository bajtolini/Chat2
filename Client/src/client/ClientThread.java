package client;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientThread extends Thread {

    private boolean stop;
    private Socket socket = null;
    private Client client = null;
    private DataInputStream streamIn = null;

    public ClientThread(Client _client, Socket _socket) {
        this.stop = false;
        client = _client;
        socket = _socket;
        open();
        start();
    }

    public void open() {
        try {
            streamIn = new DataInputStream(socket.getInputStream());
        } catch (IOException ioe) {
            System.err.println("Error getting input stream: " + ioe);
            client.stop();
        }
    }

    public void close() {
        try {
            if (streamIn != null) {
                streamIn.close();
            }
        } catch (IOException ioe) {
            System.err.println("Error closing input stream: " + ioe);
        }
    }

    @Override
    public void run() {
        while (!this.stop) {
            try {
                client.handle(streamIn.readUTF());
            } catch (IOException ioe) {
                if (!this.stop) {
                    System.err.println("Listening error: " + ioe.getMessage());
                }
                client.stop();
                this.finish();
            }
        }
    }

    public void finish() {
        this.stop = true;
    }
}
