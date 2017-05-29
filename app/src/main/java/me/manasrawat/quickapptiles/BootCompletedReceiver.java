package me.manasrawat.quickapptiles;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootCompletedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction() == "android.intent.action.BOOT_COMPLETED") {
            Intent serviceIntent = new Intent(context, AppTileService.class);
            context.startService(serviceIntent);
            Log.i("Quick App Tiles", "AppTileService started on boot completion");
        }

    }
}
