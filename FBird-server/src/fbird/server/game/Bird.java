package fbird.server.game;

public class Bird {
    private final int ID;
    
    private double velocity;
    
    private double y;
    private double rotation;
    
    private boolean dead;
    
    private int score;
    
    public Bird(int ID) {
        this.ID = ID;
        this.y = 0.0;
        this.rotation = 0.0;
        this.velocity = 0.0;
        this.dead = false;
        this.score = 0;
    }
    
    public Bird(int ID, double y, double rotation) {
        this.ID = ID;
        this.y = y;
        this.rotation = rotation;
        this.dead = false;
        this.score = 0;
    }
    
    public int getID() {
        return ID;
    }
    
    public double getVelocity() {
        return velocity;
    }
    
    public double getY() {
        return y;
    }
    
    public double getRotation() {
        return rotation;
    }
    
    public int getScore() {
        return score;
    }
    
    public boolean isDead() {
        return dead;
    }
    
    public void addVelocity(double velocity) {
        this.velocity += velocity;
    }
    
    public void addY(double y) {
        this.y += y;
    }
    
    public void addRotation(double rotation) {
        this.rotation += rotation;
    }
    
    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }
    
    public void setY(double y) {
        this.y = y;
    }
    
    public void setRotation(double rotation) {
        this.rotation = rotation;
    }
    
    public void setDead(boolean dead) {
        this.dead = dead;
    }
    
    public void setScore(int score) {
        this.score = score;
    }
}
