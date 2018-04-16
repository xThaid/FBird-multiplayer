package fbird.client.game;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.concurrent.atomic.AtomicInteger;

public class Bird {
    
    private final int ID;
    private final String label;
    
    private final AtomicInteger y;
    private final AtomicInteger rotation;
    
    private final int waveInterval;
    private int lastState = 0;
    private boolean down = false;
    private long lastTime = 0;
    
    private final float opacity;
    
    public Bird(String label, int waveInterval, float opacity, int ID) {
        this.ID = ID;
        this.label = label;
        
        y = new AtomicInteger(200);
        rotation = new AtomicInteger(0);
        
        this.waveInterval = waveInterval;
        lastTime = System.currentTimeMillis();
        
        this.opacity = opacity;
    }
    
    public void render(Graphics2D g) {
        long now = System.currentTimeMillis();
        if(now - lastTime > waveInterval) {
            if(down)
                lastState--;
            else
                lastState++;
         
            
            if(lastState == -1 || lastState == 3) {
                lastState = 1;
                down = !down;
            }
            lastTime = now;
        }
        
        
        Resource res = Resource.BIRD_IMAGES[lastState];
        
        AffineTransform at = new AffineTransform();
        at.translate(50, y.get());
        at.rotate(Math.toRadians(rotation.get()), res.getWidth() / 2, res.getHeight() / 2);
        res.draw(g, at, opacity);
        
        Font font = new Font("TimesRoman", Font.BOLD, 15);    
        at = new AffineTransform();
        at.translate(50 - 6, y.get() - 6);
        at.rotate(Math.toRadians(rotation.get()), res.getWidth() / 2 + 6, res.getHeight() / 2 + 6);
        Font rotatedFont = font.deriveFont(at);
        g.setFont(rotatedFont);
        g.drawString(label, 0, 0);
    }
    
    public void update(int y, int rotation) {
        this.y.set(y);
        this.rotation.set(rotation);
    }
    
    public int getID() {
        return ID;
    }
}
