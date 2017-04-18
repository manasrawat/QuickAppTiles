package me.cyource.qstiles;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.util.List;

import static me.cyource.qstiles.Activity.*;

/**
 * Created by manas on 09/01/2017.
 */
public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>  {

    private List<ApplicationInfo> list;
    public static int checkedPosition;

    public Adapter(List<ApplicationInfo> list) {
        this.list = list;
        checkedPosition = sharedPreferences.getInt("item", 0);
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
    public Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclee, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final ApplicationInfo info = list.get(position);
        final String label = (String) (info != null ? packMan.getApplicationLabel(info) : "(unknown)");
        final Drawable icon = packMan.getApplicationIcon(info);
        final String pack = info.packageName;

        holder.appName.setText(label);
        holder.appIcon.setImageDrawable(icon);
        holder.appSelect.setChecked(position == checkedPosition);
        holder.appSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent updateIntent = new Intent(context, Service.class);
                updateIntent.putExtra("label", label);
                updateIntent.putExtra("pack", pack);

                //Icon Encoding
                Bitmap encodee = ((BitmapDrawable) icon).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                encodee.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] bits = stream.toByteArray();

                updateIntent.putExtra("bits", bits);

                if (position != checkedPosition)  {
                    checkedPosition = position;
                    context.startService(updateIntent);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("item", position)
                    .putString("pack", pack)
                    .apply();
                    notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}
