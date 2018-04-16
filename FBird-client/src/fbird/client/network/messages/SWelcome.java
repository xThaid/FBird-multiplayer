package fbird.client.network.messages;

import fbird.client.network.EMessages;
import fbird.client.network.Message;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SWelcome extends Message {

    private int clientID;
    
    public SWelcome() {
        super(EMessages.SWelcome);
    }
    
    public SWelcome(int clientID) {
        super(EMessages.SWelcome);
        
        this.clientID = clientID;
    }
    
    public void setClientID(int id) {
        clientID = id;
    }
    
    public int getClientID() {
        return clientID;
    }

    @Override
    protected void _serialize(DataOutputStream out) throws IOException {
        out.writeInt(clientID);
    }

    @Override
    protected void unserialize(DataInputStream in) throws IOException {
        clientID = in.readInt();
    }
    
}
