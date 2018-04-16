package fbird.server.game;

public class Tube {
    private final int x;
    private final int y;
    private final int gap;
    
    public Tube(int x, int y, int gap) {
        this.x = x;
        this.y = y;
        this.gap = gap;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public int getGap() {
        return gap;
    }
}
