package fbird.server;

public class Player {
    private final int ID;
    private final String nick;
    private boolean ready;
    
    public Player(int ID, String nick, boolean ready) {
        this.ID = ID;
        this.nick = nick;
        this.ready = ready;
    }
    
    public void setReady(boolean ready) {
        this.ready = ready;
    }
    
    public int getID() {
        return ID;
    }

    public String getNick() {
        return nick;
    }

    public boolean isReady() {
        return ready;
    }
}