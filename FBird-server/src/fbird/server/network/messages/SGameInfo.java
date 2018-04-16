package fbird.server.network.messages;

import fbird.server.network.EMessages;
import fbird.server.network.Message;
import fbird.server.game.Bird;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SGameInfo extends Message {

    private int currentX;
    
    private int birdsNum;
    private Bird[] birds;
    
    public SGameInfo() {
        super(EMessages.SGameInfo);
    }
    
    public SGameInfo(int currentX, int birdsNum, Bird[] birds) {
        super(EMessages.SGameInfo);
        
        this.currentX = currentX;
        this.birdsNum = birdsNum;
        this.birds = birds;
    }
    
    public int getCurrentX() {
        return currentX;
    }
    
    public int getBirdsNum() {
        return birdsNum;
    }
    
    public Bird[] getBirdsInfo() {
        return birds;
    }

    @Override
    protected void _serialize(DataOutputStream out) throws IOException {
        out.writeInt(currentX);
        out.writeInt(birdsNum);
        for(int i = 0; i < birdsNum; i++) {
            out.writeInt(birds[i].getID());
            out.writeInt((int) birds[i].getY());
            out.writeInt((int) birds[i].getRotation());
        }
    }

    @Override
    protected void unserialize(DataInputStream in) throws IOException {
        currentX = in.readInt();
        birdsNum = in.readInt();
        
        birds = new Bird[birdsNum];
        for(int i = 0; i < birdsNum; i++) {
            int ID = in.readInt();
            int y = in.readInt();
            int rotation = in.readInt();
            birds[i] = new Bird(ID, y, rotation);
        }
    }
}
