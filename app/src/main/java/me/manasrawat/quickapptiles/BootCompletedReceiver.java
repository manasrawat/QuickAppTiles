package me.manasrawat.quickapptiles;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import cyanogenmod.os.Build;

import static android.content.Intent.ACTION_BOOT_COMPLETED;

public class BootCompletedReceiver extends BroadcastReceiver {

    private String TAG = getClass().getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {

        if (ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            if (Build.CM_VERSION.SDK_INT > 0 &&
               (android.os.Build.VERSION.SDK_INT == android.os.Build.VERSION_CODES.LOLLIPOP_MR1 ||
                android.os.Build.VERSION.SDK_INT == android.os.Build.VERSION_CODES.M)) {
                new CMTileBuilder(context, context.getPackageManager());
                Log.i(TAG, "CMTileBuilder started on boot completion");
            } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                Intent serviceIntent = new Intent(context, AppTileService.class);
                context.startService(serviceIntent);
                Log.i(TAG, "AppTileService started on boot completion");
            } else {
                Log.i(TAG, "Unsupported CM/Android version");
            }
        }
    }

}
