package fbird.client.game;

import fbird.client.Event;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Game extends JPanel {
    
    public static final int SCREEN_X = 800;
    public static final int SCREEN_Y = 500;
    
    private JFrame frame = null;
    
    private final Event<Object> exitEvent;
    private final Event<Object> jumpEvent;
    
    private final Thread gameThread;
    private boolean running = false;
    
    private final int FPS = 1000;
    
    private final AtomicInteger xOffset;
    
    private final int myID;
    
    private final Background bg;
    private final HashMap<Integer, Bird> birds;
    private final Tube[] tubes;
    
    private final long startingTime;
    private final int startDelay;
    
    private int score;
    private int frames;
    public Game(int myID, HashMap<Integer, Bird> birds, Tube[] tubes, Event<Object> exitEvent, Event<Object> jumpEvent,
            int startDelay) {
        super(true);
        
        this.myID = myID;
        
        this.birds = birds;
        this.tubes = tubes;
        this.exitEvent = exitEvent;
        this.jumpEvent = jumpEvent;
        
        this.startingTime = System.currentTimeMillis();
        this.startDelay = startDelay;
        
        this.score = -1;
        this.frames = 0;
        
        setSize(SCREEN_X, SCREEN_Y);
        
        gameThread = new Thread(() -> {
            long lastLoopTime = System.nanoTime();
            final long OPTIMAL_TIME = 1000000000 / FPS;
            long lastFpsTime = 0;
            int updates = 0;

            while(running){
                long now = System.nanoTime();
                long updateLength = now - lastLoopTime;
                if(updateLength < OPTIMAL_TIME){
                    continue;
                }
                lastLoopTime = now;
                
                lastFpsTime += updateLength;
                if(lastFpsTime >= 1000000000){
                    System.out.println("FPS: " + frames);
                    lastFpsTime = 0;
                    frames = 0;
                }

                repaint();
                updates++;
                try{
                    long sleepTime = (lastLoopTime - System.nanoTime() + OPTIMAL_TIME) / 1000000;
                    Thread.sleep(sleepTime);
                }catch(Exception e){
                }
            }
        });
        
        bg = new Background();
        xOffset = new AtomicInteger();
        
        init();
        
    }
    
    public void start() {
        running = true;
        repaint();
        frame.setVisible(true);
        gameThread.start();
    }
    
    public void stop() {
        running = false;
        try {
            gameThread.join();
        } catch (InterruptedException ex) {}
    }
    
    public void endGame(int score) {
        running = false;
        repaint();
        this.score = score;
    }
    
    public void destroyGame() {
        frame.dispose();
    }
    
    public void setX(int x) {
        xOffset.set(x);
    }
    
    public HashMap<Integer, Bird> getBirds() {
        return birds;
    }
    
    private void init() {
        frame = new JFrame("Flappy bird");
        frame.setLayout(new BorderLayout());

        setPreferredSize(new Dimension(SCREEN_X, SCREEN_Y));
        setMaximumSize(new Dimension(SCREEN_X, SCREEN_Y));
        frame.add(this, BorderLayout.CENTER);
        frame.setResizable(false);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        frame.setIgnoreRepaint(true);

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitEvent.handle(null);
                System.exit(0);
            }
        });
        
        frame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            boolean spacePressed = false;
            
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_SPACE && !spacePressed) {
                    spacePressed = true;
                    jumpEvent.handle(null);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_SPACE)
                    spacePressed = false;
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, SCREEN_X, SCREEN_Y);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);

        render(g2d);
    }
    
    private void render(Graphics2D g) {
        frames++;
        int x = xOffset.get();

        bg.render(g);
        
        for(Tube tube : tubes)
            tube.render(g, x);
        
        birds.get(myID).render(g);
        
        birds.values().stream().filter((bird) -> (bird.getID() != myID)).forEach((bird) -> {
            bird.render(g);
        });
         
        bg.renderGround(g, x);
    
        long toStart = startingTime + startDelay - System.currentTimeMillis();
        if(toStart > 0) {
            g.setFont(new Font("TimesRoman", Font.BOLD, 30));
            g.drawString(String.valueOf(toStart / 1000.0), 350, 100);
        }
     
        if(!running) {
            g.setFont(new Font("TimesRoman", Font.BOLD, 30));
            g.drawString("Koniec Gry!", 300, 100);
            g.drawString("Twój wynik: " + score, 280, 150);
        }
    }
}