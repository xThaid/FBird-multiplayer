package fbird.client.network.messages;

import fbird.client.network.EMessages;
import fbird.client.network.Message;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class CReady extends Message {

    public CReady() {
        super(EMessages.CReady);
    }

    @Override
    protected void _serialize(DataOutputStream out) throws IOException {
    
    }

    @Override
    protected void unserialize(DataInputStream in) throws IOException {
        
    }
    
}
