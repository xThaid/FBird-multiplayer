package fbird.client.game;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Resource {
    
    public static final Resource TUBE_UP = new Resource("res/tube1.png");
    public static final Resource TUBE_DOWN = new Resource("res/tube2.png");
    public static final Resource BACKGROUND = new Resource("res/bg.png");
    public static final Resource GROUND = new Resource("res/ground.png");
    public static final Resource BIRD_FULL = new Resource("res/bird.png");
    public static final Resource[] BIRD_IMAGES = {
        new Resource(BIRD_FULL.getImage().getSubimage(0, 0, 36, 26)),
        new Resource(BIRD_FULL.getImage().getSubimage(36, 0, 36, 26)),
        new Resource(BIRD_FULL.getImage().getSubimage(72, 0, 36, 26))        
    };
    
    private BufferedImage img;
    
    private final int height;
    private final int width;
    
    public Resource(String file) {
        System.out.println("Loading resource: " + file);
        img = null; 
        try {
            img = ImageIO.read(this.getClass().getClassLoader().getResourceAsStream(file));
        } catch (IOException ex) {
            System.out.println("Cannot load resource: " + file.toString());
        }
        
        img = toCompatibleImage(img);
        
        height = img.getHeight();
        width = img.getWidth();
    }
    
    public Resource(BufferedImage img) {
        this.img =  toCompatibleImage(img);
        
        height = img.getHeight();
        width = img.getWidth();
    }
    
    public void draw(Graphics2D g, AffineTransform at, float opacity) {
        setOpacity(g, opacity);
        g.drawImage(img, at, null);
    }
    
    public void draw(Graphics2D g, AffineTransform at) {
        draw(g, at, 1.0f);
    }
    
    public void draw(Graphics2D g, int x, int y, float opacity) {
        setOpacity(g, opacity);
        g.drawImage(img, x, y, width, height, null);
    }
    
    public void draw(Graphics2D g, int x, int y) {
        draw(g, x, y, 1.0f);
    }
    
    public void draw(Graphics2D g, int x, int y, int width, int height, float opacity) {
        setOpacity(g, opacity);
        g.drawImage(img, x, y, width, height, null);
    }
    
    public void draw(Graphics2D g, int x, int y, int width, int height) {
        draw(g, x, y, width, height, 1.0f);
    }
    
    public BufferedImage getImage() {
        return img;
    }
    
    public int getHeight() {
        return height;
    }
    
    public int getWidth() {
        return width;
    }

    private static void setOpacity(Graphics2D g, float opacity) {
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
    }
    
    private static BufferedImage toCompatibleImage(BufferedImage image)
    {
        // obtain the current system graphical settings
        GraphicsConfiguration gfx_config = GraphicsEnvironment.
            getLocalGraphicsEnvironment().getDefaultScreenDevice().
            getDefaultConfiguration();

        /*
         * if image is already compatible and optimized for current system 
         * settings, simply return it
         */
        if (image.getColorModel().equals(gfx_config.getColorModel()))
            return image;

        // image is not optimized, so create a new image that is
        BufferedImage new_image = gfx_config.createCompatibleImage(
                image.getWidth(), image.getHeight(), Transparency.BITMASK);

        // get the graphics context of the new image to draw the old image on
        Graphics2D g2d = (Graphics2D) new_image.getGraphics();

        // actually draw the image and dispose of context no longer needed
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();

        // return the new optimized image
        return new_image; 
    }
}