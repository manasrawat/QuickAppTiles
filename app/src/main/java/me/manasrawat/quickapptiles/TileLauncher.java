package me.manasrawat.quickapptiles;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;
import cyanogenmod.os.Build;

public class TileLauncher {

    public TileLauncher(Context context, PackageManager packMan, String TAG) {
        if (Build.CM_VERSION.SDK_INT > 0 &&
           (android.os.Build.VERSION.SDK_INT == android.os.Build.VERSION_CODES.LOLLIPOP_MR1 ||
            android.os.Build.VERSION.SDK_INT == android.os.Build.VERSION_CODES.M)) {
            new CMTileBuilder(context, packMan);
        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            context.startService(new Intent(context, AppTileService.class));
        } else {
            Toast.makeText(context, "Unsupported CM/Android version", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Unsupported CM/Android version");
        }
    }

}
