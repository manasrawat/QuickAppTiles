package me.manasrawat.quickapptiles;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import cyanogenmod.app.CMStatusBarManager;
import cyanogenmod.app.CustomTile;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

class CMTileBuilder {

    CMTileBuilder(Context context, PackageManager packMan) {
        Log.i(getClass().getSimpleName(), "CM Tile added");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        String pack = sharedPreferences.getString("pack", context.getPackageName());

        Intent prependIntent = packMan.getLaunchIntentForPackage(pack);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, prependIntent, FLAG_UPDATE_CURRENT);

        Intent settingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        settingsIntent.setData(Uri.parse("package:" + pack));
        PendingIntent pendingSettings = PendingIntent.getActivity(context, 0, settingsIntent, FLAG_UPDATE_CURRENT);

        String encoded = sharedPreferences.getString("icon", "icon");
        byte[] bits = Base64.decode(encoded.getBytes(), Base64.DEFAULT);
        Bitmap decodee = BitmapFactory.decodeByteArray(bits, 0, bits.length);

        CMStatusBarManager.getInstance(context)
                .publishTile(0, new CustomTile.Builder(context)
                        .setLabel(sharedPreferences.getString("label", context.getString(R.string.app_name)))
                        .setOnClickIntent(pendingIntent)
                        .setOnLongClickIntent(pendingSettings)
                        .setIcon(decodee)
                        .build()
                );
    }

}
