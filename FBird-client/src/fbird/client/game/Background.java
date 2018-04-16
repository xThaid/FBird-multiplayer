package fbird.client.game;

import java.awt.Graphics2D;

public class Background {
    
    public Background() {
    
    }
    
    public void renderGround(Graphics2D g, int xOffset) {
        for(int i = 0; i < 4; i++)
        {
            int x = Resource.GROUND.getWidth() * i - xOffset % Resource.GROUND.getWidth();
            
            Resource.GROUND.draw(g, x, Game.SCREEN_Y - 75);
        }
    }
    
    public void render(Graphics2D g) {
        Resource.BACKGROUND.draw(g, 0, 0, Game.SCREEN_X + 50, Game.SCREEN_Y);
    }
}
