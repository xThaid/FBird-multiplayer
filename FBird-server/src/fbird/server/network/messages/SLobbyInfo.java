package fbird.server.network.messages;

import fbird.server.Player;
import fbird.server.network.EMessages;
import fbird.server.network.Message;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SLobbyInfo extends Message {

    private int clientNum;
    private Player[] clients;
    
    public SLobbyInfo() {
        super(EMessages.SLobbyInfo);
    }
    
    public SLobbyInfo(int clientNum, Player[] clients) {
        super(EMessages.SLobbyInfo);
        
        this.clientNum = clientNum;
        this.clients = clients;
    }
    
    public int getClientNum() {
        return clientNum;
    }
    
    public Player[] getClients() {
        return clients;
    }

    @Override
    protected void _serialize(DataOutputStream out) throws IOException {
        out.writeInt(clientNum);
        for(int i = 0; i < clientNum; i++) {
            out.writeUTF(clients[i].getNick());
            out.writeBoolean(clients[i].isReady());
        }
    }

    @Override
    protected void unserialize(DataInputStream in) throws IOException {
        clientNum = in.readInt();
        clients = new Player[clientNum];
        
        for(int i = 0; i < clientNum; i++) {
            String nick = in.readUTF();
            boolean ready = in.readBoolean();
            clients[i] = new Player(-1, nick, ready);
        }
    }
}
