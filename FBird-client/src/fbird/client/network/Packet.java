package fbird.client.network;

public class Packet {
    private final byte[] data;
    
    public Packet(byte[] data) {
        this.data = data;
    }
    
    public byte[] getData() {
        return data;
    }
}
