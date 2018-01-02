package com.example.vipula.serialusbtest;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.example.vipula.serialusbtest.server.HttpServer;
import com.example.vipula.serialusbtest.server.WebsocketServer;

import org.nanohttpd.protocols.http.NanoHTTPD;
import org.nanohttpd.protocols.websockets.NanoWSD;

import java.io.IOException;

public class FrontEndService extends Service {
    private static final String TAG = "SerialUsbTest";
    public static final String ACTION_STARTALL = "com.example.vipula.serialusbtest.action.STARTALL";
    public static final String ACTION_SENDALL = "com.example.vipula.serialusbtest.action.SENDALL";
    public static final String EXTRA_HTTPPORT = "com.example.vipula.serialusbtest.extra.HTTPPORT";
    public static final String EXTRA_WEBSOCKETPORT = "com.example.vipula.serialusbtest.extra.WEBSOCKETPORT";
    public static final String EXTRA_JSONSEND = "com.example.vipula.serialusbtest.extra.JSONSEND";

    private int field = 0x00000020;

    private HttpServer httpServer;
    private WebsocketServer socketServer;


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    // This is the object that receives interactions from clients.
    private final IBinder mBinder = new FrontEndBinder();

    public FrontEndService() {  }

    @Override
    public void onCreate() {
        Log.d(TAG, "FrontEndService onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "FrontEndService onStartCommand");
        if (intent != null) {
            final String action = intent.getAction();
            Log.d(TAG, "Intent is " + action);

            if (ACTION_STARTALL.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_HTTPPORT);
                final String param2 = intent.getStringExtra(EXTRA_WEBSOCKETPORT);
                int httpPort = Integer.valueOf(param1);
                int websocketPort = Integer.valueOf(param2);
                if (httpServer == null) {
                    httpServer = new HttpServer(getApplicationContext(), httpPort);
                    try {
                        httpServer.start(0);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (socketServer == null) {
                    socketServer = new WebsocketServer(getApplicationContext(),websocketPort);
                    try {
                        socketServer.start(0);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
            else if (ACTION_SENDALL.equals(action))
            {
                final String json_message = intent.getStringExtra(EXTRA_JSONSEND);
                if(socketServer != null && socketServer.isAlive())
                {
                    socketServer.sendToAllUsers(json_message);
                }
            }
        }
        return START_STICKY;
    }



    @Override
    public void onDestroy() {
        Log.d(TAG, "FrontEndSerivce onDestroy called");
        if (socketServer != null) {
            socketServer.stop();
        }

        if (httpServer != null) {
            httpServer.stop();
        }

    };

    public class FrontEndBinder extends Binder {
        FrontEndService getService() {
            return FrontEndService.this;
        }
    }
}
