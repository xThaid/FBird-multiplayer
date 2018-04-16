package fbird.server.network;

import fbird.server.Event;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Set;

public class ConnectionManager {
    private final Thread listenerThread;
    private ServerSocket serverSocket;
    
    private final Event<Packet> msgReceivedEvent;
    
    private final HashMap<Integer, Connection> connections;
    private int lastID = 0;
    
    public ConnectionManager(int port, Event<Packet> msgReceivedEvent) {
        this.msgReceivedEvent = msgReceivedEvent;
        connections = new HashMap<>();
        listenerThread = new Thread(new ServerSocketListener());
        listenerThread.setName("Connection manager thread");
            
        serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException ex) {}
        
    }
    
    public void start() {
        listenerThread.start();
    }
    
    public void disconnect(int id) {
        connections.get(id).disconnect();
        connections.remove(id);
    }
    
    public void onNewConnection(Connection conn) {
        conn.setUpEvents(msgReceivedEvent);
        conn.setID(lastID);
        conn.start();
        connections.put(lastID++, conn);
    }
    
    public Connection getConnection(int id) {
        return connections.get(id);
    }
    
    public Set<Integer> getConnectionIDs() {
        return connections.keySet();
    }
    
    class ServerSocketListener implements Runnable {
        
        @Override
        public void run() {
            while(true) {
                Socket socket = acceptSocket();
                if(socket != null) {
                    onNewConnection(new Connection(socket));
                }
            }
        }

        private Socket acceptSocket() {
            if(serverSocket == null)
                return null;

            Socket socket = null;
            try {
                socket = serverSocket.accept();
            } catch (IOException ex) {
            }

            return socket;
        }        
    }
}