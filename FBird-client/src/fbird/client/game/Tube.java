package fbird.client.game;

import java.awt.Graphics2D;

public class Tube {
    
    private final int x;
    private final int y;
    private final int gap;
    
    public Tube(int x, int y, int gap) {
        this.x = x;
        this.y = y;
        this.gap = gap;
    }
    
    public void render(Graphics2D g, int xOffset) {
        int X = x - xOffset;
        if(X > Game.SCREEN_X || X < -100)
            return;
        
        Resource.TUBE_UP.draw(g, X, -Resource.TUBE_UP.getHeight() + y);
        Resource.TUBE_DOWN.draw(g, X, y + gap);
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