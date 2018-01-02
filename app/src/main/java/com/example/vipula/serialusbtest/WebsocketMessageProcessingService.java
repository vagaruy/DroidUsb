package com.example.vipula.serialusbtest;

import android.app.IntentService;
import android.app.KeyguardManager;
import android.content.Intent;
import android.content.Context;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.PowerManager;
import android.support.annotation.RequiresApi;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class WebsocketMessageProcessingService extends IntentService {
    private static final String TAG = "SerialUsbTest";

    private static final String ACTION_JSONRECIEVED = "com.example.vipula.serialusbtest.action.JSONRECIEVED";
    private static final String ACTION_JSONSENT = "com.example.vipula.serialusbtest.action.JSONSENT";

    private static final String EXTRA_JSONMESSAGE = "com.example.vipula.serialusbtest.extra.PARAM1";

    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;

    private boolean screen_status;

    public WebsocketMessageProcessingService() {
        super("WebsocketMessageProcessingService");
        Log.d(TAG, "Calling constructor Websocket");
        screen_status = false;
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionJsonRecieved(Context context, String json) {
        /// This service receives the JSON message from front end. Then decides which service it is destined for
        // Some of the processing are delegated to external connected system and some happen here
        Intent intent = new Intent(context, WebsocketMessageProcessingService.class);
        intent.setAction(ACTION_JSONRECIEVED);
        intent.putExtra(EXTRA_JSONMESSAGE, json);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_JSONRECIEVED.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_JSONMESSAGE);
                handleActionJsonRecieved(param1);
            } else if (ACTION_JSONSENT.equals(action)) {
                //final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                //final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                //handleActionBaz(param1, param2);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionJsonRecieved(String param1) {
        Log.d(TAG, "JSon message recieved from front end " + param1);
        try {
            JSONObject obj = new JSONObject(param1);
            if (obj.get("Client").equals("Backend")) {
                Log.d(TAG, "Json is for us.");

                if (obj.has("Request")) {
                    if (obj.get("Request").equals("Battery")) {
                        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                        Intent batteryStatus = getApplicationContext().registerReceiver(null, ifilter);
                        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                        float batteryPct = level / (float) scale;
                        Log.d(TAG, "Calculated battery is " + batteryPct);

                        String sendObj = new JSONObject().put("Battery", batteryPct).put("Client", "Frontend").toString();
                        Intent serviceIntent = new Intent(this, FrontEndService.class);
                        serviceIntent.setAction(FrontEndService.ACTION_SENDALL);
                        serviceIntent.putExtra(FrontEndService.EXTRA_JSONSEND,sendObj);
                        startService(serviceIntent);
                    }
                    else if (obj.get("Request").equals("Screen"))
                    {
                        if(screen_status)
                        {
                            turnOffScreen();
                            screen_status = false;
                        }
                        else
                        {
                            turnOnScreen();
                            screen_status = true;
                        }
                    }

                }            }
        } catch (JSONException e) {
            e.printStackTrace();
        }   }

        private KeyguardManager km;
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void turnOnScreen(){
        // turn on screen
        Log.v(TAG, "Turn Screen ON!");
        if(km == null | powerManager == null || wakeLock == null) {
            KeyguardManager keyguardManager = (KeyguardManager)getSystemService(Context.KEYGUARD_SERVICE);
            KeyguardManager.KeyguardLock keyguardLock =  keyguardManager.newKeyguardLock(TAG);
            keyguardLock.disableKeyguard();
            powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
            assert powerManager != null;
            wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, TAG);
        }
        wakeLock.acquire();
    }

    public void turnOffScreen()
    {
        Log.v(TAG, "Turn Screen OFF!");
        if(wakeLock !=null && wakeLock.isHeld())
        {
            wakeLock.release();
        }
    }

//    /**
//     * Handle action Baz in the provided background thread with the provided
//     * parameters.
//     */
//    private void handleActionBaz(String param1, String param2) {
//        // TODO: Handle action Baz
//        throw new UnsupportedOperationException("Not yet implemented");
//    }
}
