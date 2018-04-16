package fbird.client.network;

import fbird.client.Event;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Connection {
    private Socket socket;
    
    private DataInputStream netReader;
    private DataOutputStream netWriter;

    private final Thread netThread;
    private final SocketListener listener;
    
    private Event<Packet> msgReceivedEvent;
    
    public Connection(Socket socket) {
        this.socket = socket;
        
        try {
            netReader = new DataInputStream(socket.getInputStream());
            netWriter = new DataOutputStream(socket.getOutputStream());
        } catch (IOException ex) {
        }
        
        listener = new SocketListener();
        netThread = new Thread(listener);
        netThread.setName("Connection thread");
    }

    public void start() {
        netThread.start();
    }
    
    public void disconnect() {
        listener.running = false;
        
        netReader = null;
        netWriter = null;
        
        socket = null;
        
        try {
            netThread.join();
        } catch (InterruptedException ex) {    
        }
    }
    
    public void send(Packet packet) {
        try {
            netWriter.writeInt(packet.getData().length);
            netWriter.write(packet.getData());
            netWriter.flush();
        } catch (IOException ex) { }
    }
   
    public void setUpEvents(Event<Packet> msgReceivedEvent) {
        this.msgReceivedEvent = msgReceivedEvent;
    }
    
    private void readPacket()
    {
        byte[] temp = new byte[1];
        
        int res = -1;
        try {
            int len =  netReader.readInt();
            temp = new byte[len];
            res = netReader.read(temp);
        } catch (IOException ex) {
        }
        
        if(res != -1)
            msgReceivedEvent.handle(new Packet(temp));
    }
    
    class SocketListener implements Runnable {

        public boolean running = true;
        
        @Override
        public void run() {
            while(running) {
                readPacket();
            }
        }
    }
}
