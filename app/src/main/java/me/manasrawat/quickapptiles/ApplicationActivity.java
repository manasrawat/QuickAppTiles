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

import static android.content.Intent.*;
import static android.service.quicksettings.TileService.ACTION_QS_TILE_PREFERENCES;

public class ApplicationActivity extends AppCompatActivity {

    public static LinearLayoutManager layoutManager;
    static Context context;
    static PackageManager packMan;
    static SharedPreferences sharedPreferences;
    private List<ApplicationInfo> trimmedList;
    private BroadcastReceiver additionOrRemovalReceiver;
    private RecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);

        context = getApplicationContext();
        packMan = getPackageManager();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (ACTION_QS_TILE_PREFERENCES.equals(getIntent().getAction())) {
            Intent settingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            settingsIntent.setData(Uri.parse("package:" + sharedPreferences.getString("pack", getPackageName())));
            startActivity(settingsIntent);
        }

        //RecyclerViewAdapter inflation
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclees);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        trimmedList = new ArrayList<>();
        getApps();
        adapter = new RecyclerViewAdapter(trimmedList);
        recyclerView.setAdapter(adapter);
        layoutManager.scrollToPositionWithOffset(adapter.checkedPosition, 0);

        additionOrRemovalReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int i;
                boolean cont = true;
                if (ACTION_PACKAGE_REMOVED.equals(intent.getAction()) ||
                        (ACTION_PACKAGE_CHANGED.equals(intent.getAction()))) {
                    String packageName = intent.getData().getSchemeSpecificPart();
                    for (i = 0; i < trimmedList.size() && cont; i++)
                        if (trimmedList.get(i).packageName.equals(packageName)) cont = false;
                    adapter.removeAt(i - 1);
                }
                List<ApplicationInfo> previousList = new ArrayList<>(trimmedList);
                trimmedList.clear();
                getApps();
                if (ACTION_PACKAGE_ADDED.equals(intent.getAction()) ||
                        (ACTION_PACKAGE_CHANGED.equals(intent.getAction()) &&
                                previousList.size() < trimmedList.size())) {
                    for (i = 0; i < trimmedList.size() && cont; i++)
                        if ((i < previousList.size() &&
                                !(previousList.get(i).packageName.equals(trimmedList.get(i).packageName))) ||
                                i == trimmedList.size() - 1) cont = false;
                    adapter.insertAt(i - 1);
                    layoutManager.scrollToPositionWithOffset(i - 1, 0);
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addDataScheme("package");
        intentFilter.addAction(ACTION_PACKAGE_ADDED);
        intentFilter.addAction(ACTION_PACKAGE_REMOVED);
        intentFilter.addAction(ACTION_PACKAGE_CHANGED);
        registerReceiver(additionOrRemovalReceiver, intentFilter);
    }

    private void getApps() {
        List<ApplicationInfo> list = packMan.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo appInfo : list) if (packMan.getLaunchIntentForPackage(appInfo.packageName) != null)
            trimmedList.add(appInfo);
        Collections.sort(trimmedList, new ApplicationInfo.DisplayNameComparator(packMan));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(additionOrRemovalReceiver);
    }

}
