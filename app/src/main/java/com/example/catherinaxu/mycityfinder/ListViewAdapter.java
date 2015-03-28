package com.example.catherinaxu.mycityfinder;

import android.content.Context;
import android.graphics.Typeface;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

public class ListViewAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private List<String> mLocations;
    private SparseBooleanArray mSelected;
    private Typeface font_reg;

    public ListViewAdapter(Context context, List<String> locations, String FONT) {
        super(context, R.layout.layout_list_item, locations);

        mContext = context;
        mLocations = locations;
        mSelected = new SparseBooleanArray();
        font_reg = Typeface.createFromAsset(context.getAssets(), FONT);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_list_item, null);
            holder = new ViewHolder();
            holder.listItem = (TextView) convertView.findViewById(R.id.list_item);
            holder.listItem.setTypeface(font_reg);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.listItem.setText(getItem(position));


        if (mSelected.get(position) == true) {
            holder.checkBox.setChecked(true);
        } else {
            holder.checkBox.setChecked(false);
        }

        mSelected.put(position, holder.checkBox.isChecked());

        return convertView;
    }

    public void markSelected(int position, boolean selected) {
        mSelected.put(position, selected);
    }

    public void deselectAll() {
        for (int i = 0; i < mSelected.size(); i++) {
            mSelected.put(i, false);
        }
    }

    private static class ViewHolder {
        TextView listItem;
        CheckBox checkBox;
    }
}
