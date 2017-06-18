package me.manasrawat.quickapptiles;

import android.content.*;
import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.*;

import static me.manasrawat.quickapptiles.ApplicationActivity.*;

class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>  {

    int checkedPosition;
    private List<ApplicationInfo> list;
    private int lastCheckedPosition;
    private String TAG = getClass().getSimpleName();
    private SharedPreferences.Editor editor;

    RecyclerViewAdapter(List<ApplicationInfo> theList) {
        list = theList;
        editor = sharedPreferences.edit();

        if (sharedPreferences.getBoolean("initial", true)) {
            editor.putBoolean("initial", false).apply();
            checkedPosition = -1;
            ApplicationInfo info = context.getApplicationInfo();
            selectAndUpdate(checkedPosition, packMan.getApplicationIcon(info), info.packageName, "Select App");
        } else {
            int i; boolean cont; for (i = 0, cont = true; i < list.size() && cont; i++)
                if (list.get(i).packageName.equals(sharedPreferences.getString("pack", context.getPackageName())))
                    cont = false;
            if (cont) {
                checkedPosition = -1;
            } else {
                checkedPosition = i - 1;
            }
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView appIcon;
        TextView appName;
        RadioButton appSelect;
        RelativeLayout appRecyclee;

        ViewHolder(View v) {
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
        return new ViewHolder(v);
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

                    selectAndUpdate(adapterPosition, icon, pack, label);

                    Log.i(TAG, label + " selected");
                    new AppTileLauncher(context, packMan, TAG, false);
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

    void removeAt(int i) {
        list.remove(i);
        notifyItemRemoved(i);
        boolean check = false;
        if (checkedPosition > i) {
            editor.putInt("item", checkedPosition - 1).apply();
            check = true;
        }
        if (checkedPosition == i) {
            editor.putInt("item", -1).apply();
            checkedPosition = sharedPreferences.getInt("item", -1);
            layoutManager.scrollToPositionWithOffset(0, 0);
        }
        if (check) checkedPosition = sharedPreferences.getInt("item", -1);
        notifyItemRangeChanged(i, list.size());
    }

    void insertAt(int i) {
        notifyItemInserted(i);
        if (checkedPosition >= i) {
            editor.putInt("item", checkedPosition + 1).apply();
            checkedPosition = sharedPreferences.getInt("item", -1);
        }
        notifyItemRangeChanged(i, list.size());
    }

    private void selectAndUpdate(int adapterPosition, Drawable icon, String pack, String label) {
        lastCheckedPosition = checkedPosition;
        checkedPosition = adapterPosition;
        editor.putInt("item", adapterPosition).apply();
        new SelectorAndUpdater(context, icon, pack, label);
    }

}
