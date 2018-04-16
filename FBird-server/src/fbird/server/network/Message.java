package fbird.server.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class Message {
    
    private final EMessages type;
    private int ID;

    public Message(EMessages type) {
        this.type = type;
    }
    
    public EMessages getType() {
        return type;
    }
    
    public void setID(int ID) {
        this.ID = ID;
    }
    
    public int getID() {
        return ID;
    }
    
    public void serialize(DataOutputStream out) throws IOException {
        out.writeInt(type.getCode());

        _serialize(out);
    }
    
    protected abstract void _serialize(DataOutputStream out) throws IOException;
    protected abstract void unserialize(DataInputStream in) throws IOException;
}
