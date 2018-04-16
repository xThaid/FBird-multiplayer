package fbird.client.network.messages;

import fbird.client.network.EMessages;
import fbird.client.network.Message;
import fbird.client.Player;
import fbird.client.game.Tube;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SGameReady extends Message {
    
    private int playersNum;
    private Player[] players;
    
    private int tubesNum;
    private Tube[] tubes;
    
    private long startTime;
    
    public SGameReady() {
        super(EMessages.SGameReady);
    }
    
    public SGameReady(int playersNum, Player[] players, int tubesNum, Tube[] tubes, long startTime) {
        super(EMessages.SGameReady);
        
        this.playersNum = playersNum;
        this.players = players;
        
        this.tubesNum = tubesNum;
        this.tubes = tubes;
        
        this.startTime = startTime;
    }
    
    public int getPlayersNum() {
        return playersNum;
    }
    
    public Player[] getPlayers() {
        return players;
    }
    
    public int getTubesNum() {
        return tubesNum;
    }
    
    public Tube[] getTubes() {
        return tubes;
    }
    
    public long getStartTime() {
        return startTime;
    }

    @Override
    protected void _serialize(DataOutputStream out) throws IOException {
        out.writeInt(playersNum);
        for(int i = 0; i < playersNum; i++) {
            out.writeInt(players[i].getID());
            out.writeUTF(players[i].getNick());
        }
        
        out.writeInt(tubesNum);
        for(int i = 0; i < tubesNum; i++) {
            out.writeInt(tubes[i].getX());
            out.writeInt(tubes[i].getY());
            out.writeInt(tubes[i].getGap());
        }
        
        out.writeLong(startTime);
    }

    @Override
    protected void unserialize(DataInputStream in) throws IOException {
        playersNum = in.readInt();
        players = new Player[playersNum];
        for(int i = 0; i < playersNum; i++) {
            int ID = in.readInt();
            String nick = in.readUTF();
            players[i] = new Player(ID, nick, false);
        }
        
        tubesNum = in.readInt();
        tubes = new Tube[tubesNum];
        for(int i = 0; i < tubesNum; i++) {
            int x = in.readInt();
            int y = in.readInt();
            int gap = in.readInt();
            tubes[i] = new Tube(x, y, gap);
        }
        
        startTime = in.readLong();
    }
}
