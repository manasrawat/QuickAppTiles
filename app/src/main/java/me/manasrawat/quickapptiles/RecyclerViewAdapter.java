package me.manasrawat.quickapptiles;

import android.content.*;
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
import java.util.*;

import static me.manasrawat.quickapptiles.ApplicationActivity.*;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>  {

    public List<ApplicationInfo> list;
    public int lastCheckedPosition, checkedPosition;
    private String TAG = getClass().getSimpleName();
    private Intent serviceIntent;
    private SharedPreferences.Editor editor;

    public RecyclerViewAdapter(List<ApplicationInfo> theList) {
        list = theList;
        serviceIntent = new Intent(context, AppTileService.class);
        editor = sharedPreferences.edit();

        int i;
        boolean cont = true;
        if (sharedPreferences.getBoolean("initial", true)) {
            editor.putBoolean("initial", false).apply();
            for (i = 0; i <  list.size() && cont; i++)
                if (list.get(i).packageName.equals(context.getPackageName())) cont = false;
            checkedPosition = i - 1;
            setFor(checkedPosition);
        } else {
            for (i = 0; i < list.size() && cont; i++)
                if (list.get(i).packageName.equals(sharedPreferences.getString("pack", context.getPackageName())))
                    cont = false;
            if (cont) {
                checkedPosition = sharedPreferences.getInt("item", -1);
                if (checkedPosition == list.size()) checkedPosition--;
                setFor(checkedPosition);
                context.startService(serviceIntent);
            } else {
                checkedPosition = i - 1;
            }
        }
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

                    selectAndUpdate(icon, adapterPosition, pack, label);

                    Log.i(TAG, label + " selected");
                    if (Build.CM_VERSION.SDK_INT > 0 &&
                       (android.os.Build.VERSION.SDK_INT == android.os.Build.VERSION_CODES.LOLLIPOP_MR1 ||
                        android.os.Build.VERSION.SDK_INT == android.os.Build.VERSION_CODES.M)) {
                        new CMTileBuilder(context, packMan);
                    } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
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
            setFor(i);
            context.startService(serviceIntent);

        }
        if (checkedPosition > i) {
            editor.putInt("item", checkedPosition - 1).apply();
            checkedPosition = sharedPreferences.getInt("item", 0);
        }
        notifyItemRangeChanged(i, list.size());
    }

    public void insertAt(int i) {
        notifyItemInserted(i);
        if (checkedPosition >= i) {
            editor.putInt("item", checkedPosition + 1).apply();
            checkedPosition = sharedPreferences.getInt("item", 0);
        }
        notifyItemRangeChanged(i, list.size());
    }

    public void setFor(int i) {
        ApplicationInfo info = list.get(i);
        String label = (String) packMan.getApplicationLabel(info), pack = info.packageName;
        Drawable icon = packMan.getApplicationIcon(info);
        selectAndUpdate(icon, i, pack, label);
    }

    public void selectAndUpdate(Drawable icon, int adapterPosition, String pack, String label) {

        //Icon Encoding
        Bitmap encodee = ((BitmapDrawable) icon).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        encodee.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] bits = stream.toByteArray();
        String encoded = Base64.encodeToString(bits, Base64.DEFAULT);

        lastCheckedPosition = checkedPosition;
        checkedPosition = adapterPosition;

        editor.putInt("item", adapterPosition)
              .putString("pack", pack)
              .putString("label", label)
              .putString("icon", encoded)
              .apply();
    }

}
