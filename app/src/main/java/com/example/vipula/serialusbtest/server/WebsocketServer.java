package com.example.vipula.serialusbtest.server;

import android.content.Context;
import android.provider.ContactsContract;
import android.util.Log;

import com.example.vipula.serialusbtest.WebsocketMessageProcessingService;

import org.nanohttpd.protocols.http.IHTTPSession;
import org.nanohttpd.protocols.http.response.Response;
import org.nanohttpd.protocols.websockets.CloseCode;
import org.nanohttpd.protocols.websockets.NanoWSD;
import org.nanohttpd.protocols.websockets.WebSocket;
import org.nanohttpd.protocols.websockets.WebSocketFrame;
import org.nanohttpd.samples.websockets.DebugWebSocketServer;

import java.io.IOException;
import java.util.logging.Level;

/**
 * Created by vipula on 12/29/17.
 */

public class WebsocketServer extends NanoWSD {
    private final boolean debug;
    private Context Ctx;

    private UserList userList;

    private static final String TAG = "SerialUsbTest";

    public WebsocketServer (Context ctx, int port) {
        super(port);
        Ctx = ctx;
        debug = false;
        userList = new UserList();
    }

    public void sendToAllUsers(String json_file)
    {
        userList.sendToAll(json_file);
    }

    @Override
    public Response handleWebSocket(IHTTPSession session) {
        return super.handleWebSocket(session);
    }

    @Override
    protected WebSocket openWebSocket(IHTTPSession handshake) {
        return new  CommunicationWebsocket(this, handshake);
    }

    private class CommunicationWebsocket extends WebSocket {
        private final WebsocketServer server;
        User user;
        IHTTPSession httpSession;

        public CommunicationWebsocket(WebsocketServer server, IHTTPSession handshakeRequest) {
            super(handshakeRequest);
            this.server = server;
            user = new User();
            user.webSocket = this;
            userList.addUser(user);
            this.httpSession = handshakeRequest;
        }

        @Override
        protected void onOpen() {
            Log.d(TAG,"Websocket opened");
        }

        @Override
        protected void onClose(CloseCode code, String reason, boolean initiatedByRemote) {
            if (server.debug) {
                Log.d(TAG, "C [" + (initiatedByRemote ? "Remote" : "Self") + "] " + (code != null ? code : "UnknownCloseCode[" + code + "]")
                        + (reason != null && !reason.isEmpty() ? ": " + reason : ""));
            }
            userList.removeUser(user);
        }

        @Override
        protected void onMessage(WebSocketFrame message) {
            ///This is coming from an individual socket. Common sink for that data somewhere and then send it to everyone from there.
            Log.d(TAG, " Message is " + message.getTextPayload());
            WebsocketMessageProcessingService.startActionJsonRecieved(Ctx, message.getTextPayload());
//                message.setUnmasked();
//                sendFrame(message);
        }

        @Override
        protected void onPong(WebSocketFrame pong) {
            if (server.debug) {
                Log.d(TAG,"P " + pong);
            }
        }

        @Override
        protected void onException(IOException exception) {
            Log.d(TAG,"exception occured" +exception);
        }

        @Override
        protected void debugFrameReceived(WebSocketFrame frame) {
            if (server.debug) {
                Log.d(TAG,"R " + frame);
            }
        }

        @Override
        protected void debugFrameSent(WebSocketFrame frame) {
            if (server.debug) {
                Log.d(TAG,"S " + frame);
            }
        }
    }
}
