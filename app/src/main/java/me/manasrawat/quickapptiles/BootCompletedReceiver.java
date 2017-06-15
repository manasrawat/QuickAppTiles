package me.manasrawat.quickapptiles;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static android.content.Intent.ACTION_BOOT_COMPLETED;

public class BootCompletedReceiver extends BroadcastReceiver {

    private final String TAG = getClass().getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {

        if (ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            new AppTileLauncher(context, context.getPackageManager(), TAG, true);
            Log.i(TAG, "Device booted");
        }
    }

}
