package me.manasrawat.quickapptiles;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Base64;
import android.util.Log;

@TargetApi(Build.VERSION_CODES.N)
public class AppTileService extends TileService {

    private String pack;
    private final String TAG = getClass().getSimpleName();

    @Override
    public void onTileAdded() {
        requestListeningState(this, new ComponentName(this, getClass()));
        Log.i(TAG, "Tile added");
        super.onTileAdded();
    }

    @Override
    public void onTileRemoved() {
        Log.i(TAG, "Tile removed");
        super.onTileRemoved();
    }

    @Override
    public void onStartListening () {
        Log.i(TAG, "Started listening");
        Tile tile = getQsTile();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String label = sharedPreferences.getString("label", getString(R.string.app_name));
        pack = sharedPreferences.getString("pack", getPackageName());
        tile.setLabel(label);

        String encoded = sharedPreferences.getString("icon", "icon");
        byte[] bits = Base64.decode(encoded.getBytes(), Base64.DEFAULT);
        Bitmap decodee = BitmapFactory.decodeByteArray(bits, 0, bits.length);
        Icon icon = Icon.createWithBitmap(decodee);
        tile.setIcon(icon);

        tile.setState(pack.equals(getPackageName()) ? Tile.STATE_INACTIVE : Tile.STATE_ACTIVE );
        tile.updateTile();
        Log.i(TAG, "Updated tile");
        super.onStartListening();
    }

    @Override
    public void onStopListening () {
        Log.i(TAG, "Stopped listening");
        super.onStopListening();
    }

    @Override
    public void onClick() {
        if (isLocked()) {
            unlockAndRun(new Runnable() {
                @Override
                public void run() {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            launchApp();
                        }
                    }, 200);
                }
            });
        } else {
            launchApp();
        }
    }

    private void launchApp() {
        PackageManager packMan = getPackageManager();
        Intent launch = packMan.getLaunchIntentForPackage(pack);
        if (launch == null) {
            launch = packMan.getLaunchIntentForPackage(getPackageName());
        }
        startActivityAndCollapse(launch);
    }

}
