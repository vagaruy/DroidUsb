package com.example.vipula.serialusbtest.server;

import android.util.Log;

import org.nanohttpd.protocols.websockets.CloseCode;
import org.nanohttpd.protocols.websockets.WebSocket;

import java.io.IOException;
import java.util.Vector;

/**
 * Created by vipula on 12/30/17.
 */

public class UserList {
    private static final String TAG = "SerialUsbTest";
    Vector<User> list;

    public UserList() {
        list = new Vector<User>();
    }

    public void addUser(User user) {
        list.add(user);
        Log.d(TAG, "Adding user to websocket list");
    }

    public void removeUser(User user) {
        list.remove(user);
        Log.d(TAG, "Removing user to websocket list");
        Log.d(TAG, "User count is " + list.size());
    }

    public int userCount() {
        Log.d(TAG, "User count is " + list.size());
        return list.size();
    }

    public void sendToAll(String str) {
        for (int i = 0; i < list.size(); i++) {
            User user = list.get(i);
            WebSocket ws = user.webSocket;
            if (ws != null) {
                try {
                    ws.send(str);
                } catch (IOException e) {
                    System.out.println("sending error.....");
                    try {
                        ws.close(CloseCode.InvalidFramePayloadData,"stuff happened",false);
                    } catch (IOException e1) {
                        removeUser(user);
                    }
                }
            }
        }
    }

    public void disconectAll() {
        for (int i = 0; i < list.size(); i++) {
            User user = list.get(i);
            WebSocket ws = user.webSocket;
            if(ws != null){
                try {
                    ws.close(CloseCode.InvalidFramePayloadData, "reqrement",false);
                } catch (IOException e) {
                    removeUser(user);
                }
            }
        }
    }
}