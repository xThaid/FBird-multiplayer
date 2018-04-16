package fbird.client.network.messages.types;

public class BirdInfo {
    private final int ID;
    private final int y;
    private final int rotation;
    
    public BirdInfo(int ID, int y, int rotation) {
        this.ID = ID;
        this.y = y;
        this.rotation = rotation;
    }
    
    public int getID() {
        return ID;
    }
    
    public int getY() {
        return y;
    }
    
    public int getRotation() {
        return rotation;
    }
}
