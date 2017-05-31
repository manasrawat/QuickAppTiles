package me.manasrawat.quickapptiles;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.preference.PreferenceManager;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Base64;
import android.util.Log;

/**
 * Created by manas on 09/01/2017.
 */

@SuppressLint("Override")
@TargetApi(Build.VERSION_CODES.N)
public class AppTileService extends TileService {

    private String pack, TAG = getClass().getSimpleName();
    private Tile tile;
    private Intent launch;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        requestListeningState(getApplicationContext(), new ComponentName(getApplicationContext(), AppTileService.class));
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onTileAdded() {
        super.onTileAdded();
        Log.i(TAG, "Tile added");
        requestListeningState(getApplicationContext(), new ComponentName(getApplicationContext(), AppTileService.class));

    }

    @Override
    public void onStartListening () {
        super.onStartListening();
        Log.i(TAG, "Started listening");

        tile = getQsTile();
        tile.setState(Tile.STATE_ACTIVE);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String label = sharedPreferences.getString("label", getString(R.string.app_name));
        pack = sharedPreferences.getString("pack", getApplicationContext().getPackageName());
        tile.setLabel(label);

        String encoded = sharedPreferences.getString("icon", "icon");
        byte[] bits = Base64.decode(encoded.getBytes(), Base64.DEFAULT);
        Bitmap decodee = BitmapFactory.decodeByteArray(bits, 0, bits.length);
        Icon icon = Icon.createWithBitmap(decodee);
        tile.setIcon(icon);

        tile.updateTile();
    }

    @Override
    public void onStopListening () {
        super.onStartListening();
        Log.i(TAG, "Stopped listening");
    }

    @Override
    public void onClick() {
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        if (keyguardManager.inKeyguardRestrictedInputMode()) {
            Log.i(TAG, "Device is locked");
            unlockAndRun(new Runnable() {
                @Override
                public void run() {
                    launchApp();
                }
            });
        } else {
            Log.i(TAG, "Device is unlocked");
            launchApp();
        }
    }

    public void launchApp() {
        launch = getPackageManager().getLaunchIntentForPackage(pack);
        startActivityAndCollapse(launch);
    }

}
