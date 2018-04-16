package fbird.client.network.messages;

import fbird.client.network.EMessages;
import fbird.client.network.Message;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class CBye extends Message {

    public CBye() {
        super(EMessages.CBye);
    }

    @Override
    protected void _serialize(DataOutputStream out) throws IOException {
    
    }

    @Override
    protected void unserialize(DataInputStream in) throws IOException {
        
    }
    
}
