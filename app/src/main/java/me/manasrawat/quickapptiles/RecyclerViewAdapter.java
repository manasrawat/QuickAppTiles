package me.manasrawat.quickapptiles;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import cyanogenmod.app.CMStatusBarManager;
import cyanogenmod.app.CustomTile;
import cyanogenmod.os.Build;

import java.io.ByteArrayOutputStream;
import java.util.List;

import static me.manasrawat.quickapptiles.ApplicationActivity.*;

/**
 * Created by manas on 09/01/2017.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>  {

    private List<ApplicationInfo> list;
    public static int checkedPosition;

    public RecyclerViewAdapter(List<ApplicationInfo> list) {
        this.list = list;
        checkedPosition = sharedPreferences.getInt("item", -1);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView appIcon;
        public TextView appName;
        public RadioButton appSelect;

        public ViewHolder(View v) {
            super(v);
            appIcon = (ImageView) v.findViewById(R.id.appIcon);
            appName = (TextView) v.findViewById(R.id.appName);
            appSelect = (RadioButton) v.findViewById(R.id.appSelect);
        }
    }


    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclee, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final ApplicationInfo info = list.get(position);
        final String label = (String) packMan.getApplicationLabel(info), pack = info.packageName;
        final Drawable icon = packMan.getApplicationIcon(info);

        holder.appName.setText(label);
        holder.appIcon.setImageDrawable(icon);
        holder.appSelect.setChecked(position == checkedPosition);
        holder.appSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (position != checkedPosition)  {
                    Intent serviceIntent = new Intent(context, AppTileService.class);

                    //Icon Encoding
                    Bitmap encodee = ((BitmapDrawable) icon).getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    encodee.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] bits = stream.toByteArray();
                    String encoded = Base64.encodeToString(bits, Base64.DEFAULT);

                    checkedPosition = position;
                    if (Build.CM_VERSION.SDK_INT > 0 && (android.os.Build.VERSION.RELEASE == "5.1" || android.os.Build.VERSION.RELEASE == "6.0")) {
                        Intent prependIntent = packMan.getLaunchIntentForPackage(pack);
                        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, prependIntent, 0);
                        Intent settingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        settingsIntent.addCategory(Intent.CATEGORY_DEFAULT)
                                .setData(Uri.parse("package:" + pack));
                        PendingIntent pendingSettings = PendingIntent.getActivity(context, 0, settingsIntent, 0);
                        CustomTile tile = new CustomTile.Builder(context)
                                .setLabel(label)
                                .setOnClickIntent(pendingIntent)
                                .setOnSettingsClickIntent(null)
                                .setOnLongClickIntent(pendingSettings)
                                .setContentDescription(null)
                                .setIcon(encodee)
                                .hasSensitiveData(false)
                                .build();
                        CMStatusBarManager.getInstance(context)
                                .publishTile(1, tile);
                    } else {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("item", position)
                              .putString("pack", pack)
                              .putString("label", label)
                              .putString("icon", encoded)
                              .apply();
                        notifyDataSetChanged();
                        context.startService(serviceIntent);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}
