package me.manasrawat.quickapptiles;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import cyanogenmod.os.Build;

import java.io.ByteArrayOutputStream;
import java.util.List;

import static me.manasrawat.quickapptiles.ApplicationActivity.*;

/**
 * Created by manas on 09/01/2017.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>  {

    private List<ApplicationInfo> list;
    private String TAG = getClass().getSimpleName();
    public static int checkedPosition;
    //public static final int CUSTOM_TILE_ID = 1;

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

                    //Icon Encoding
                    Bitmap encodee = ((BitmapDrawable) icon).getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    encodee.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] bits = stream.toByteArray();
                    String encoded = Base64.encodeToString(bits, Base64.DEFAULT);

                    checkedPosition = position;

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("item", position)
                            .putString("pack", pack)
                            .putString("label", label)
                            .putString("icon", encoded)
                            .apply();

                    if (Build.CM_VERSION.SDK_INT > 0 &&
                       (android.os.Build.VERSION.SDK_INT == android.os.Build.VERSION_CODES.LOLLIPOP_MR1 ||
                        android.os.Build.VERSION.SDK_INT == android.os.Build.VERSION_CODES.M)) {
                        new CMTileBuilder(context, packMan);
                    } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        Intent serviceIntent = new Intent(context, AppTileService.class);
                        context.startService(serviceIntent);
                    } else {
                        Toast.makeText(context, "Unsupported CM/Android version", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "Unsupported CM/Android version");
                    }
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
