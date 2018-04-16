package fbird.server.network.messages;

import fbird.server.network.EMessages;
import fbird.server.network.Message;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SGameEnd extends Message {

    private int score;
    
    public SGameEnd() {
        super(EMessages.SGameEnd);
    }
    
    public SGameEnd(int score) {
        super(EMessages.SGameEnd);
        this.score = score;
    }

    public int getScore() {
        return score;
    }
    
    @Override
    protected void _serialize(DataOutputStream out) throws IOException {
        out.writeInt(score);
    }

    @Override
    protected void unserialize(DataInputStream in) throws IOException {
        score = in.readInt();
    }
    
}
