package me.manasrawat.quickapptiles;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.preference.PreferenceManager;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Base64;

/**
 * Created by manas on 09/01/2017.
 */

@SuppressWarnings("All")
public class AppTileService extends TileService {

    private String label, pack;
    private Icon icon;
    private SharedPreferences sharedPreferences;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        requestListeningState(getApplicationContext(), new ComponentName(getApplicationContext(), AppTileService.class));
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onTileAdded() {
        requestListeningState(getApplicationContext(), new ComponentName(getApplicationContext(), AppTileService.class));
    }

    @Override
    public void onStartListening () {
        Tile tile = getQsTile();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        label = sharedPreferences.getString("label", "Quick App Tiles");
        pack = sharedPreferences.getString("pack", getApplicationContext().getPackageName());
        tile.setLabel(label);

        String encoded = sharedPreferences.getString("icon", "icon");
        byte[] bits = Base64.decode(encoded.getBytes(), Base64.DEFAULT);
        Bitmap decodee = BitmapFactory.decodeByteArray(bits, 0, bits.length);
        icon = Icon.createWithBitmap(decodee);
        tile.setIcon(icon);

        tile.updateTile();
    }

    @Override
    public void onClick() {
        Intent launch = getPackageManager().getLaunchIntentForPackage(pack);
        launch.addCategory(Intent.CATEGORY_LAUNCHER);
        startActivityAndCollapse(launch);
    }

}
