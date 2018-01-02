package com.example.vipula.serialusbtest.server;


import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.nanohttpd.protocols.http.IHTTPSession;
import org.nanohttpd.protocols.http.NanoHTTPD;
import org.nanohttpd.protocols.http.request.Method;
import org.nanohttpd.protocols.http.response.IStatus;
import org.nanohttpd.protocols.http.response.Response;
import org.nanohttpd.protocols.http.response.Status;
import org.nanohttpd.protocols.websockets.WebSocket;
import org.nanohttpd.protocols.websockets.WebSocketFrame;

/**
 * Created by vipula on 12/29/17.
 */

public class HttpServer extends NanoHTTPD {
    private static final String TAG = "SerialUsbTest";

    private Context Ctx;
    public HttpServer(Context ctx, int port) {
        super( port);
        Ctx = ctx;
    }

    @Override
    public Response handle(IHTTPSession session) throws IOException {

        String uri = session.getUri();
        Log.d(TAG,"URI IS " + uri);

        final StringBuilder buf = new StringBuilder();
//        for (Entry<Object, Object> kv : header.entrySet())
//            buf.append(kv.getKey() + " : " + kv.getValue() + "\n");
        InputStream mbuffer = null;

        try {
            if(uri!=null){

                if(uri.contains(".js")){
                    mbuffer = Ctx.getAssets().open(uri.substring(1));
                    return Response.newChunkedResponse(Status.OK,NanoHTTPD.MIME_js, mbuffer);
                }else if(uri.contains(".css")){
                    mbuffer = Ctx.getAssets().open(uri.substring(1));
                    return Response.newChunkedResponse(Status.OK, MIME_CSS, mbuffer);

                }else if(uri.contains(".png")) {
                    mbuffer = Ctx.getAssets().open(uri.substring(1));
                    // Status.OK = "200 OK" or Status.OK = Status.OK;(check comments)
                    return Response.newChunkedResponse(Status.OK, MIME_png, mbuffer);
                }
//                else if (uri.contains("/mnt/sdcard")){
//                    Log.d(TAG,"request for media on sdCard "+uri);
//                    File request = new File(uri);
//                    mbuffer = new FileInputStream(request);
//                    FileNameMap fileNameMap = URLConnection.getFileNameMap();
//                    String mimeType = fileNameMap.getContentTypeFor(uri);
//                    Response streamResponse = new Response(Status.OK, mimeType, mbuffer);
//                    Random rnd = new Random();
//                    String etag = Integer.toHexString( rnd.nextInt() );
//                    streamResponse.addHeader( "ETag", etag);
//                    streamResponse.addHeader( "Connection", "Keep-alive");
//                    return streamResponse;}
                    else{
                    mbuffer = Ctx.getAssets().open("index.html");
                    return Response.newChunkedResponse(Status.OK, MIME_HTML, mbuffer);
                }
            }

        } catch (IOException e) {
            Log.d(TAG,"Error opening file"+uri.substring(1));
            e.printStackTrace();
        }

        return null;

    }
}