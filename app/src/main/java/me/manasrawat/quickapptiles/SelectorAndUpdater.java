package me.manasrawat.quickapptiles;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class SelectorAndUpdater {

    public SelectorAndUpdater(Context context, Drawable icon, String pack, String label) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Bitmap encodee = ((BitmapDrawable) icon).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        encodee.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] bits = stream.toByteArray();
        String encoded = Base64.encodeToString(bits, Base64.DEFAULT);

        sharedPreferences.edit()
                .putString("pack", pack)
                .putString("label", label)
                .putString("icon", encoded)
                .apply();
    }

}
