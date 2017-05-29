package me.manasrawat.quickapptiles;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ApplicationActivity extends AppCompatActivity {

    public static Context context;
    public static PackageManager packMan;
    public static SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);

        context = getApplicationContext();
        packMan = getPackageManager();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        //RecyclerViewAdapter inflation
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclees);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        List<ApplicationInfo> list = packMan.getInstalledApplications(PackageManager.GET_META_DATA), trimmedList = new ArrayList<>();
        for (ApplicationInfo appInfo : list) if (packMan.getLaunchIntentForPackage(appInfo.packageName) != null) trimmedList.add(appInfo);
        Collections.sort(trimmedList, new ApplicationInfo.DisplayNameComparator(packMan));
        RecyclerView.Adapter adapter = new RecyclerViewAdapter(trimmedList);
        recyclerView.setAdapter(adapter);
    }

}
