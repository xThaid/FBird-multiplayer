package fbird.client.network;

import fbird.client.network.messages.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public enum EMessages {
    Unknown(0, Unknown.class),
    CHello(1, CHello.class),
    SWelcome(2, SWelcome.class),
    CBye(3, CBye.class),
    CReady(4, CReady.class),
    SLobbyInfo(5, SLobbyInfo.class),
    SGameReady(6, SGameReady.class),
    CJump(7, CJump.class),
    SGameInfo(8, SGameInfo.class),
    SGameEnd(9, SGameEnd.class);
    
    private final int code;
    private final Class msgClass;
    EMessages(int code, Class msgClass) {
        this.code = code;
        this.msgClass = msgClass;
    }
    
    public int getCode() {
        return code;
    }
    
    public Message getInstance() {
        try {
            Constructor constr = msgClass.getConstructor();
            return (Message) constr.newInstance();
        } catch (NoSuchMethodException | SecurityException ex) {
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
        }
        
        return null;
    }
    
    public static EMessages getEnum(int code) {
        if(values.containsKey(code)) {
            return EMessages.values.get(code);
        }
        return EMessages.Unknown;
    }

    private static final HashMap<Integer, EMessages> values = new HashMap<>();

    static {
        for (final EMessages type : EMessages.values()) {
            EMessages.values.put(type.getCode(), type);
        }
    }
}
