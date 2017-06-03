package me.manasrawat.quickapptiles;

import android.content.*;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.content.Intent.ACTION_PACKAGE_ADDED;
import static android.content.Intent.ACTION_PACKAGE_REMOVED;
import static android.service.quicksettings.TileService.ACTION_QS_TILE_PREFERENCES;

public class ApplicationActivity extends AppCompatActivity {

    public static Context context;
    public static PackageManager packMan;
    public static SharedPreferences sharedPreferences;
    public static RecyclerView recyclerView;
    private List<ApplicationInfo> trimmedList;
    private BroadcastReceiver additionOrRemovalReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);

        context = getApplicationContext();
        packMan = getPackageManager();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (ACTION_QS_TILE_PREFERENCES.equals(getIntent().getAction())) {
            Intent settingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            settingsIntent.setData(Uri.parse("package:" + sharedPreferences.getString("pack", context.getPackageName())));
            startActivity(settingsIntent);
        }

        //RecyclerViewAdapter inflation
        recyclerView = (RecyclerView) findViewById(R.id.recyclees);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        trimmedList = new ArrayList<>();
        getApps();
        final RecyclerViewAdapter adapter = new RecyclerViewAdapter(trimmedList);
        recyclerView.setAdapter(adapter);

        additionOrRemovalReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String packageName = intent.getData().getSchemeSpecificPart();
                switch (intent.getAction()) {
                    case ACTION_PACKAGE_REMOVED:
                        int i; boolean cont = true; for (i = 0; i < trimmedList.size() && cont; i++)
                            if (trimmedList.get(i).packageName.equals(packageName)) cont = false;
                        adapter.removeAt(i - 1);
                    break;
                    case ACTION_PACKAGE_ADDED:
                        List<ApplicationInfo> previousList = new ArrayList<>(trimmedList);
                        trimmedList.clear();
                        getApps();
                        cont = true; for (i = 0; i < trimmedList.size() && cont; i++)
                            if ((i < previousList.size() && !(previousList.get(i).packageName.equals(trimmedList.get(i).packageName))) || i == trimmedList.size() - 1) cont = false;
                        adapter.insertAt(i - 1);
                        break;
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addDataScheme("package");
        intentFilter.addAction(ACTION_PACKAGE_ADDED);
        intentFilter.addAction(ACTION_PACKAGE_REMOVED);
        registerReceiver(additionOrRemovalReceiver, intentFilter);
    }

    public void getApps() {
        List<ApplicationInfo> list = packMan.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo appInfo : list) if (packMan.getLaunchIntentForPackage(appInfo.packageName) != null) trimmedList.add(appInfo);
        Collections.sort(trimmedList, new ApplicationInfo.DisplayNameComparator(packMan));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(additionOrRemovalReceiver);
    }

}
