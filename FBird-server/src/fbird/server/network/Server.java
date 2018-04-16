package fbird.server.network;

import fbird.server.network.messages.SWelcome;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public abstract class Server {
    private final ConnectionManager manager;
    
    private final Object callbackLock = new Object();
    private final Queue<Message> callbackQueue;
    
    public Server(int port) {
        manager = new ConnectionManager(port, (Packet obj) -> {
            onMsgReceived(obj);
        });
        manager.start();
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
    
    protected synchronized void broadcast(Message msg) {
        Set<Integer> ids = manager.getConnectionIDs();
        
        for(Integer i : ids) {
            send(i, msg);
        }
    }
    
    protected synchronized void send(int ID, Message msg) {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(byteOut);
        try {
            msg.serialize(out);
        } catch (IOException ex) {}
        Connection conn = manager.getConnection(ID);
        if(conn != null)
            conn.send(new Packet(-1, byteOut.toByteArray()));
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
        msg.setID(packet.getID());
     
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
            case CHello:
                send(msg.getID(), new SWelcome(msg.getID()));
                break;
                
            case CBye:
                manager.disconnect(msg.getID());
                break;
                
            default:
                break;
        }
        
        handleMsg(msg, msg.getID());
    }
    
    public abstract void handleMsg(Message msg, int sender);
    
}