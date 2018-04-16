package fbird.client.network;

import fbird.client.Event;
import fbird.client.network.messages.CHello;
import fbird.client.network.messages.SWelcome;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public abstract class Client {
    
    private Connection connection;
    private final Event<Packet> msgReceivedEvent;
    
    private final Object callbackLock = new Object();
    private final Queue<Message> callbackQueue;
    
    protected int ID;
    protected boolean connected = false;
    
    public Client() {
        msgReceivedEvent = (Packet obj) -> {
            onMsgReceived(obj);
        };
        
        callbackQueue = new LinkedList<>();
    }
    
    protected void startLoop() {
        while(true) {
            checkForCallbacks();
            try {
                Thread.sleep(1);
            } catch (InterruptedException ex) {}
        }
    }
    
    protected void connect(String address, int port, String nick) {
        Socket sock = null;
        try {
            sock = new Socket(address, port);
        } catch (IOException ex) {
        }
        connection = new Connection(sock);
        connection.setUpEvents(msgReceivedEvent);
        connection.start();
        send(new CHello(nick));
        connected = true;
    }
    
    protected void send(Message msg) {
        
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(byteOut);
        try {
            msg.serialize(out);
        } catch (IOException ex) {}
        
        connection.send(new Packet(byteOut.toByteArray()));
    }
    
    private void onMsgReceived(Packet packet) {
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(packet.getData()));
        int code = 0;
        try {
            code = in.readInt();
        } catch (IOException ex) {
        }
        
        Message msg = EMessages.getEnum(code).getInstance();
        
        try {
            msg.unserialize(in);
        } catch (IOException ex) {
        }
     
        postCallback(msg);
    }
    
    private Message getCallback() {
        synchronized (callbackLock) {
            if (!callbackQueue.isEmpty()) {
                return callbackQueue.poll();
            }
        }

        return null;
    }

    private Message waitForCallback() {
        synchronized (callbackLock) {
            if (callbackQueue.isEmpty()) {
                try {
                    callbackLock.wait();
                } catch (final InterruptedException e) {
                }
            }

            return callbackQueue.poll();
        }
    }

    private void postCallback(Message msg) {
        if (msg == null) {
            return;
        }

        synchronized (callbackLock) {
            callbackQueue.offer(msg);
            callbackLock.notify();
        }
    }
    
    private boolean checkForCallbacks() {
        Message msg = getCallback();
        if(msg != null) {
            _handleMsg(msg);
            return true;
        }
        
        return false;
    }
    
    private void _handleMsg(Message msg) {
        switch(msg.getType()) {
            case SWelcome:
                ID = ((SWelcome) msg).getClientID();
                break;
            default:
                break;
        }
        
        handleMsg(msg);
    }
    
    public abstract void handleMsg(Message msg);
}
