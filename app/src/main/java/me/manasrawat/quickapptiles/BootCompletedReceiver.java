package me.manasrawat.quickapptiles;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static android.content.Intent.ACTION_BOOT_COMPLETED;

public class BootCompletedReceiver extends BroadcastReceiver {

    private String TAG = getClass().getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {

        if (ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent serviceIntent = new Intent(context, AppTileService.class);
            context.startService(serviceIntent);
            Log.i(TAG, "AppTileService started on boot completion");
        }

    }
}
