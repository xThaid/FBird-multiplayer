package fbird.server.game;

import fbird.server.Event;
import fbird.server.network.messages.SGameInfo;
import java.util.Random;

public class GameServer {
    
    private static final int MIN_BIRD_Y = 2500;
    
    public static final int WAITING_TIME = 5000;
    
    public static final int BIRD_Y_START = 150;
    
    private static final int UPS = 80;
    
    private static final double MAP_SPEED = 2;
    private static final double GRAVITY = 60;
    private static final double JUMP_FORCE = -70;
    
    private static final int NUM_OF_TUBES = 50;
    private static final int TUBE_X_START = 1500;
    private static final int TUBE_X_GAP = 250;
    private static final int TUBE_Y_GAP = 90;
    
    private final Event<SGameInfo> updateEvent;
    private final Event<String> addLog;
    private final Event<Object> endEvent;
    
    private double currentX;
    
    private Tube[] tubes;
    private Bird[] birds;
    
    private long startingTime;
    
    private boolean running = false;
    private final Thread serverThread;

    private int currentTube;
    
    public GameServer(Event<SGameInfo> updateEvent, Event<String> addLog, Event<Object> endEvent) {
        this.updateEvent = updateEvent;
        this.addLog = addLog;
        this.endEvent = endEvent;
        
        serverThread = new Thread(() -> {
            serverLoop();
        });
    }
    
    public void init(Bird[] birds) {
        startingTime = System.currentTimeMillis();
        this.birds = birds;
        tubes = randomTubes(NUM_OF_TUBES);
        currentTube = 0;
        currentX = 0;
    }
    
    public void startGame() {
        running = true;
        serverThread.start();
    }
    
    public Tube[] getTubes() {
        return tubes;
    }
    
    public Bird[] getBirds() {
        return birds;
    }
    
    public void jump(int ID) {
        for(Bird bird : birds) {
            if(bird.getID() == ID && !bird.isDead())
                bird.setVelocity(JUMP_FORCE); 
        }
    }
    
    private void serverLoop() {
        long lastLoopTime = System.nanoTime();
        final long OPTIMAL_TIME = 1000000000 / UPS;
        long lastFpsTime = 0;
        int updates = 0;

        while(running){
            long now = System.nanoTime();
            long updateLength = now - lastLoopTime;
            if(updateLength < OPTIMAL_TIME){
                continue;
            }
            lastLoopTime = now;
            double delta = updateLength / ((double)OPTIMAL_TIME);

            lastFpsTime += updateLength;
            if(lastFpsTime >= 1000000000){
                addLog.handle("UPS: " + updates);
                lastFpsTime = 0;
                updates = 0;
            }

            update(delta);
            updates++;
            try{
                long sleepTime = (lastLoopTime - System.nanoTime() + OPTIMAL_TIME) / 1000000;
                Thread.sleep(sleepTime);
            }catch(Exception e){
            }
        }
    } 
    
    private void update(double delta) {
        currentX += delta * MAP_SPEED;
        
        if(System.currentTimeMillis() - startingTime > GameServer.WAITING_TIME) {
            boolean areAllDead = true;
            for(Bird bird : birds) {
                if(!bird.isDead()) {
                    areAllDead = false;
                    bird.setScore(currentTube);
                }
                
                if(bird.getY() > MIN_BIRD_Y)
                    continue;
                    
                
                bird.addVelocity(GRAVITY * delta / 20.0);
                bird.addY(bird.getVelocity() * delta / 20.0);
                
                
                double v = bird.getVelocity();
                if(v < 0) 
                    bird.setRotation(lerp(0, 50, v / 100.0));
                else if(v > 0)
                    bird.setRotation(lerp(0, 75, v / 100.0));
                else 
                    bird.setRotation(0);
                
                boolean collision = checkForCollisions(bird, tubes[currentTube]);
                boolean border = checkForBorders(bird);
                if(collision || border) {
                    bird.setDead(true);
                }
            }
            
            if(areAllDead) {
                endEvent.handle(null);
                running = false;
            }
        }
        
        
        
        if(currentTube != NUM_OF_TUBES - 1) {
            if(currentX > tubes[currentTube].getX() + 50) {
                currentTube++;
            }
        }
        
        updateEvent.handle(new SGameInfo((int) currentX, birds.length, birds));
    }
    
    private boolean checkForBorders(Bird bird) {
        return bird.getY() < - 100 || bird.getY() + 26 > 425;
    }
    
    private boolean checkForCollisions(Bird bird, Tube tube) {
        if(inTube(tube)) {
            int down = tube.getY() + tube.getGap();
            int up = tube.getY();
            
            return bird.getY() < up || bird.getY() + 26 > down;
        }
        return false;
    }
    
    private boolean inTube(Tube tube) {
        return currentX + 35 + 50 > tube.getX() && tube.getX() + 50 > currentX + 50;
    }
    
    private static Tube[] randomTubes(int count) {
        Tube[] tubes = new Tube[count];
        
        Random rand = new Random();
        
        for(int i = 0; i < count; i++) {
            int x = TUBE_X_START + i * TUBE_X_GAP;
            int y = rand.nextInt(250) + 50;
            int gap = TUBE_Y_GAP;
            
            Tube tube = new Tube(x, y, gap);
            tubes[i] = tube;
        }
        
        return tubes;
    }
    
    private static double lerp(double a, double b, double f) {
        if(f > 1.0)
            f = 1.0;
        return a + f * (b - a);
    }
}
