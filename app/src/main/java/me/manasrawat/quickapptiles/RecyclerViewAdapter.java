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
import android.widget.*;
import cyanogenmod.os.Build;

import java.io.ByteArrayOutputStream;
import java.util.List;

import static me.manasrawat.quickapptiles.ApplicationActivity.*;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>  {

    public List<ApplicationInfo> list;
    public int lastCheckedPosition, checkedPosition;
    private String TAG = getClass().getSimpleName();

    public RecyclerViewAdapter(List<ApplicationInfo> list) {
        this.list = list;
        checkedPosition = sharedPreferences.getInt("item", -1);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView appIcon;
        public TextView appName;
        public RadioButton appSelect;
        public RelativeLayout appRecyclee;

        public ViewHolder(View v) {
            super(v);
            appIcon = (ImageView) v.findViewById(R.id.appIcon);
            appName = (TextView) v.findViewById(R.id.appName);
            appSelect = (RadioButton) v.findViewById(R.id.appSelect);
            appRecyclee = (RelativeLayout) v.findViewById(R.id.appRecyclee);
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

        final int adapterPosition = holder.getAdapterPosition();
        final ApplicationInfo info = list.get(adapterPosition);
        final String label = (String) packMan.getApplicationLabel(info), pack = info.packageName;
        final Drawable icon = packMan.getApplicationIcon(info);

        holder.appName.setText(label);
        holder.appIcon.setImageDrawable(icon);
        holder.appSelect.setChecked(adapterPosition == checkedPosition);

        View.OnClickListener onSelection = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (adapterPosition != checkedPosition)  {

                    //Icon Encoding
                    Bitmap encodee = ((BitmapDrawable) icon).getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    encodee.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] bits = stream.toByteArray();
                    String encoded = Base64.encodeToString(bits, Base64.DEFAULT);

                    lastCheckedPosition = checkedPosition;
                    checkedPosition = adapterPosition;

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("item", adapterPosition)
                          .putString("pack", pack)
                          .putString("label", label)
                          .putString("icon", encoded)
                          .apply();

                    Log.i(TAG, label + " selected");
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

                    notifyItemChanged(lastCheckedPosition);
                    notifyItemChanged(checkedPosition);
                }
            }
        };

        holder.appSelect.setOnClickListener(onSelection);
        holder.appRecyclee.setOnClickListener(onSelection);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void removeAt(int i) {
        list.remove(i);
        notifyItemRemoved(i);
        if (checkedPosition == i) {
            if (i == list.size()) i--;
            ViewHolder viewHolder = (ViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
            viewHolder.appSelect.performClick();
        }
        if (checkedPosition > i) {
            sharedPreferences.edit().putInt("item", checkedPosition - 1).apply();
            checkedPosition = sharedPreferences.getInt("item", 0);
        }
        notifyItemRangeChanged(i, list.size());
    }

    public void insertAt(int i) {
        notifyItemInserted(i);
        if (checkedPosition >= i) {
            sharedPreferences.edit().putInt("item", checkedPosition + 1).apply();
            checkedPosition = sharedPreferences.getInt("item", 0);
        }
        notifyItemRangeChanged(i, list.size());
    }

}
