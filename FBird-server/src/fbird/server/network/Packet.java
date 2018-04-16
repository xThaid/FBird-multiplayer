package fbird.server.network;

public class Packet {
    private final int id;
    private final byte[] data;
    
    public Packet(int id, byte[] data) {
        this.id = id;
        this.data = data;
    }
    
    public int getID() {
        return id;
    }
    
    public byte[] getData() {
        return data;
    }
}
