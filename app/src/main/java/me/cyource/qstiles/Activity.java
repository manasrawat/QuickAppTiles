package me.cyource.qstiles;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Activity extends AppCompatActivity {

    //Global static vars
    public static PackageManager packMan;
    public static SharedPreferences sharedPreferences;
    public static Context context;
    public static ApplicationInfo info;
    public String pack;
    public String thisPack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);

        context = getApplicationContext();

        //Vars initialisation
        thisPack = context.getPackageName();
        packMan = getPackageManager();
        context = getApplicationContext();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        pack = sharedPreferences.getString("pack", thisPack);

        try {
            info = packMan.getApplicationInfo(pack, 0);
        } catch (PackageManager.NameNotFoundException e) {
            info = null;
        }
        //Adapter inflation
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclees);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        List<ApplicationInfo> list = packMan.getInstalledApplications(PackageManager.GET_META_DATA);
        List<ApplicationInfo> trimmedList = new ArrayList<>();
        for (ApplicationInfo appInfo : list) {
            if (packMan.getLaunchIntentForPackage(appInfo.packageName) != null) {
                trimmedList.add(appInfo);
            }
        }
        Collections.sort(trimmedList, new ApplicationInfo.DisplayNameComparator(packMan));
        RecyclerView.Adapter adapter = new Adapter(trimmedList);
        recyclerView.setAdapter(adapter);

    }

}
