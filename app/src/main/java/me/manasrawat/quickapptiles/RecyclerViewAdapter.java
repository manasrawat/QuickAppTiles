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

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>  {

    public List<ApplicationInfo> list;
    public int lastCheckedPosition, checkedPosition;
    private String TAG = getClass().getSimpleName();
    private SharedPreferences.Editor editor;

    public RecyclerViewAdapter(List<ApplicationInfo> theList) {
        list = theList;
        editor = sharedPreferences.edit();

        if (sharedPreferences.getBoolean("initial", true)) {
            editor.putBoolean("initial", false).apply();
            checkedPosition = findDefaultPos();
            setFor(checkedPosition);
        } else {
            int i; boolean cont; for (i = 0, cont = true; i < list.size() && cont; i++)
                if (list.get(i).packageName.equals(sharedPreferences.getString("pack", context.getPackageName())))
                    cont = false;
            if (cont) {
                checkedPosition = sharedPreferences.getInt("item", -1);
                if (checkedPosition == list.size()) checkedPosition--;
                setFor(checkedPosition);
                new TileLauncher(context, packMan, TAG);
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

                    selectAndUpdate(adapterPosition, icon, pack, label);

                    Log.i(TAG, label + " selected");
                    new TileLauncher(context, packMan, TAG);
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

    public int findDefaultPos() {
        int i; boolean cont; for (i = 0, cont = true; i <  list.size() && cont; i++)
            if (list.get(i).packageName.equals(context.getPackageName())) cont = false;
        return i - 1;
    }

    public void removeAt(int i) {
        list.remove(i);
        notifyItemRemoved(i);
        if (checkedPosition > i) {
            editor.putInt("item", checkedPosition - 1).apply();
            checkedPosition = sharedPreferences.getInt("item", -1);
        }
        if (checkedPosition == i) {
            int j = findDefaultPos();
            editor.putInt("item", j).apply();
            checkedPosition = sharedPreferences.getInt("item", -1);
            layoutManager.scrollToPositionWithOffset(j, 0);
        }
        notifyItemRangeChanged(i, list.size());
    }

    public void insertAt(int i) {
        notifyItemInserted(i);
        if (checkedPosition >= i) {
            editor.putInt("item", checkedPosition + 1).apply();
            checkedPosition = sharedPreferences.getInt("item", -1);
        }
        notifyItemRangeChanged(i, list.size());
    }

    public void setFor(int i) {
        ApplicationInfo info = list.get(i);
        String label = (String) packMan.getApplicationLabel(info), pack = info.packageName;
        Drawable icon = packMan.getApplicationIcon(info);
        selectAndUpdate(i, icon, pack, label);
    }

    public void selectAndUpdate(int adapterPosition, Drawable icon, String pack, String label) {
        lastCheckedPosition = checkedPosition;
        checkedPosition = adapterPosition;
        editor.putInt("item", adapterPosition).apply();
        new SelectorAndUpdater(context, icon, pack, label);
    }

}
