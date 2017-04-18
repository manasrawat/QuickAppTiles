package me.cyource.qstiles;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

/**
 * Created by manas on 09/01/2017.
 */

public class Service extends TileService {

    private Tile tile;
    private String pack;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //Application Label
        String label = intent.getStringExtra("label");
        tile.setLabel(label);

        pack = intent.getStringExtra("pack");

        //Icon Decoding
        byte[] bits = intent.getByteArrayExtra("bits");
        Bitmap decodee = BitmapFactory.decodeByteArray(bits, 0, bits.length);
        Icon icon = Icon.createWithBitmap(decodee);

        tile.setIcon(icon);
        tile.updateTile();
        return START_NOT_STICKY;
    }

    @Override
    public void onClick() {
        tile = getQsTile();
        Intent launch = getPackageManager().getLaunchIntentForPackage(pack);
        launch.addCategory(Intent.CATEGORY_LAUNCHER);
        startActivityAndCollapse(launch);
    }

}
