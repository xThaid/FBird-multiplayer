package fbird.server;

import fbird.server.game.Bird;
import fbird.server.game.GameServer;
import fbird.server.game.Tube;
import fbird.server.network.Message;
import fbird.server.network.Server;
import fbird.server.network.messages.CHello;
import fbird.server.network.messages.SGameEnd;
import fbird.server.network.messages.SGameInfo;
import fbird.server.network.messages.SGameReady;
import fbird.server.network.messages.SLobbyInfo;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class FBirdServer extends Server {

    private final GUI gui;
    
    private final HashMap<Integer, Player> players;
    private GameServer gs;
    
    public FBirdServer() {
        super(10079);
        
        gs = null;
        players = new HashMap<>();
        
        gui = new GUI();
        gui.setVisible(true);
        log("Flappy Bird server");
        log("Made by Jakub Urbańczyk\n");
        try {
            log("Server's IP: " + InetAddress.getLocalHost());
        } catch (UnknownHostException ex) {  }
        log("Good luck and have fun!");
        startLoop();
    }
    
    public final void log(String str) {
        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("[HH:mm:ss] ");
        String time = format.format(now);

        gui.addLog(time + str);
    }
    
    @Override
    public void handleMsg(Message msg, int sender) {
        log("Received message: " + msg.getType().toString() + "[" + sender + "]");
        switch(msg.getType()) {
            case CHello:
                String nick = ((CHello) msg).getNick();
                if(nick.length() > 10)
                    nick = nick.substring(0, 10);
                else if(nick.length() == 0) {
                    nick = "Flappy" + sender;
                }
                
                players.put(sender, new Player(sender, nick, false));
                log(players.get(sender).getNick() + " has joined!");
                broadcastClientInfo();
                break;
            case CBye:
                log(players.get(sender).getNick() + " has left!");
                players.remove(sender);
                broadcastClientInfo();
                break;
            case CReady:
                players.get(sender).setReady(!players.get(sender).isReady());
                broadcastClientInfo();
                checkForGameStart();
                break;
            case CJump:
                if(isGameRunning())
                    gs.jump(sender);
                break;
            default:
                break;
        }
    }
    
    public boolean isGameRunning() {
        return gs != null;
    }
    
    private void makeGameServer() {
        gs = new GameServer((SGameInfo obj) -> {
            broadcast(obj);
        }, (String str) -> {
            log(str);
        }, (Object obj) -> {
            log("Game has ended. Cleaning up");
            for(Bird bird : gs.getBirds())
                send(bird.getID(), new SGameEnd(bird.getScore()));
            gs = null;
            for(Player player : players.values())
                player.setReady(false);
            broadcastClientInfo();
        });
    }
    
    private void broadcastClientInfo() {
        broadcast(new SLobbyInfo(players.size(), players.values().toArray(new Player[0])));
    }
    
    private void checkForGameStart() {
        boolean allReady = true;
        for(Player p : players.values()) {
            if(!p.isReady()) {
                allReady = false;
            }
        }
        
        if(allReady) {
            log("Everyone is ready. It is time to start the game!");
            Bird[] birds = new Bird[players.size()];
            int counter = 0;
            for(Player p : players.values()) {
                birds[counter++] = new Bird(p.getID(), GameServer.BIRD_Y_START, 0);
            }
            
            
            makeGameServer();
            gs.init(birds);
            
            int playersNum = players.size();
            Player[] _players = players.values().toArray(new Player[0]);
            int tubesNum = gs.getTubes().length;
            Tube[] tubes = gs.getTubes();
            
            broadcast(new SGameReady(playersNum, _players, tubesNum, tubes, GameServer.WAITING_TIME));
            gs.startGame();
        }
    }
    
    public static void main(String[] args) {
        FBirdServer fBirdServer = new FBirdServer();
    } 
}
