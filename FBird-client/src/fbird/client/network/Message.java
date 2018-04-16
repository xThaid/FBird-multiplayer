package fbird.client.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class Message {
    
    private final EMessages type;
    
    public Message(EMessages type) {
        this.type = type;
    }
    
    public EMessages getType() {
        return type;
    }
    
    public void serialize(DataOutputStream out) throws IOException {
        out.writeInt(type.getCode());

        _serialize(out);
    }
    
    protected abstract void _serialize(DataOutputStream out) throws IOException;
    protected abstract void unserialize(DataInputStream in) throws IOException;
}
