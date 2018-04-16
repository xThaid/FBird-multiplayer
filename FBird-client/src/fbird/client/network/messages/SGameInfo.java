package fbird.client.network.messages;

import fbird.client.network.EMessages;
import fbird.client.network.Message;
import fbird.client.network.messages.types.BirdInfo;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SGameInfo extends Message {

    private int currentX;
    
    private int birdsNum;
    private BirdInfo[] birds;
    
    public SGameInfo() {
        super(EMessages.SGameInfo);
    }
    
    public SGameInfo(int currentX, int birdsNum, BirdInfo[] birds) {
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
    
    public BirdInfo[] getBirdsInfo() {
        return birds;
    }

    @Override
    protected void _serialize(DataOutputStream out) throws IOException {
        out.writeInt(currentX);
        out.writeInt(birdsNum);
        for(int i = 0; i < birdsNum; i++) {
            out.writeInt(birds[i].getID());
            out.writeInt(birds[i].getY());
            out.writeInt(birds[i].getRotation());
        }
    }

    @Override
    protected void unserialize(DataInputStream in) throws IOException {
        currentX = in.readInt();
        birdsNum = in.readInt();
        
        birds = new BirdInfo[birdsNum];
        for(int i = 0; i < birdsNum; i++) {
            int ID = in.readInt();
            int y = in.readInt();
            int rotation = in.readInt();
            birds[i] = new BirdInfo(ID, y, rotation);
        }
    }
}
