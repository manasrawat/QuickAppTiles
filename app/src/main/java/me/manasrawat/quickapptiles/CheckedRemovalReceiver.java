package me.manasrawat.quickapptiles;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;

import static android.content.Intent.ACTION_PACKAGE_CHANGED;
import static android.content.Intent.ACTION_PACKAGE_REMOVED;

public class CheckedRemovalReceiver extends BroadcastReceiver {

    private final String TAG = getClass().getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        if (intent.getData().getSchemeSpecificPart().equals(sharedPreferences.getString("pack", context.getPackageName())) &&
           (ACTION_PACKAGE_REMOVED.equals(intent.getAction()) || ACTION_PACKAGE_CHANGED.equals(intent.getAction()))) {
            new SelectorAndUpdater(context,
                                   ContextCompat.getDrawable(context, R.mipmap.icon),
                                   context.getPackageName(),
                                   context.getString(R.string.app_name));
            new AppTileLauncher(context, context.getPackageManager(), TAG, false);
        }
    }

}
