package fbird.client.network.messages;

import fbird.client.network.EMessages;
import fbird.client.network.Message;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class CHello extends Message {

    private String nick;
    
    public CHello() {
        super(EMessages.CHello);
    }
    
    public CHello(String nick) {
        super(EMessages.CHello);
        
        this.nick = nick;
    }
    
    public void setNick(String nick) {
        this.nick = nick;
    }
    
    public String getNick() {
        return nick;
    }

    @Override
    protected void _serialize(DataOutputStream out) throws IOException {
        out.writeUTF(nick);
    }

    @Override
    protected void unserialize(DataInputStream in) throws IOException {
        nick = in.readUTF();
    }
    
}
