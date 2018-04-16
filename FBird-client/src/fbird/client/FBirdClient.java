package fbird.client;

import fbird.client.game.Bird;
import fbird.client.game.Game;
import fbird.client.network.Client;
import fbird.client.network.Message;
import fbird.client.network.messages.CBye;
import fbird.client.network.messages.CJump;
import fbird.client.network.messages.CReady;
import fbird.client.network.messages.SGameEnd;
import fbird.client.network.messages.SGameInfo;
import fbird.client.network.messages.SGameReady;
import fbird.client.network.messages.SLobbyInfo;
import fbird.client.network.messages.types.BirdInfo;
import java.awt.Color;
import java.util.HashMap;
import java.util.Random;

public class FBirdClient extends Client {

    private final GUI gui;
    
    private Game game;
    
    public FBirdClient() {
        super();
        gui = new GUI(new Event<Object>() {
            @Override
            public void handle(Object obj) {
                connect(gui.getAddress(), 10079, gui.getNick());
            }
        }, (Object obj) -> {
            send(new CReady());
        }, (Object obj) -> {
            if(connected)
                send(new CBye());
        });
        gui.setVisible(true);
        
        startLoop();
    }

    @Override
    public void handleMsg(Message msg) {
        switch(msg.getType()) {
            case SWelcome:
                gui.connectedState();
                break;
            case SLobbyInfo:
                SLobbyInfo info = (SLobbyInfo) msg;
                updateLobbyInfo(info.getClients());
                break;
            case SGameReady:
                gui.setVisible(false);
                SGameReady gRdy = (SGameReady) msg;
                HashMap<Integer, Bird> birds = new HashMap<>();
                Random rand = new Random();
                for(int i = 0; i < gRdy.getPlayersNum(); i++) {
                    float opacity = gRdy.getPlayers()[i].getID() == ID ? 1.0f : 0.5f;
                    int id = gRdy.getPlayers()[i].getID();
                    Bird bird = new Bird(gRdy.getPlayers()[i].getNick(), rand.nextInt(300) + 50, opacity, id);
                    birds.put(id, bird);
                }
                game = new Game(ID, birds, gRdy.getTubes(), (Object obj) -> {
                    send(new CBye());
                },(Object obj) -> {
                    send(new CJump());
                }, (int) gRdy.getStartTime());
                game.start();
                break;
            case SGameInfo:
                if(game == null)
                    break;
                SGameInfo gInfo = (SGameInfo) msg;
                game.setX(gInfo.getCurrentX());
                for(BirdInfo bird : gInfo.getBirdsInfo()) {
                    game.getBirds().get(bird.getID()).update(bird.getY(), bird.getRotation());
                }
                break;
            case SGameEnd:
                game.endGame(((SGameEnd) msg).getScore());
                
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ex) {}
                
                game.destroyGame();
                game = null;
                gui.setVisible(true);
                break;
            default:
                break;
        }
    }
    
    private void updateLobbyInfo(Player[] clients) {
        Color[] colors = new Color[clients.length + 1];
        String[] nicks = new String[clients.length];
        for(int i = 0; i < clients.length; i++) {
            colors[i] = (clients[i].isReady() ? Color.green : Color.red);
            nicks[i] = clients[i].getNick();
        }
        
        gui.setList(nicks, colors);
    }
    
    public static void main(String[] args) {
        try {
            Class.forName("fbird.client.game.Resource");
        } catch (ClassNotFoundException ex) { }
        
        FBirdClient client = new FBirdClient();
    }
}
