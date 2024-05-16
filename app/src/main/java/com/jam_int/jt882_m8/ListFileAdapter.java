package com.jam_int.jt882_m8;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ListFileAdapter extends ArrayAdapter<String> {

    /**
     * List of files name
     */
    ArrayList<String> Items;
    /**
     * List of boolean that indicate if a file is selected
     */
    ArrayList<Boolean> Selected;

    /**
     * Constructor for list of files
     * <p>
     * TODO for make it better and cleaner i can create a class with string and bool for name and selected
     *
     * @param context
     * @param items
     * @param selected
     */
    public ListFileAdapter(Activity context, ArrayList<String> items, ArrayList<Boolean> selected) {
        super(context, R.layout.listfileadapter, items);
        Items = items;
        Selected = selected;
    }

    /**
     * Function for apply effects based on the state of the row
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listfileadapter, parent, false);
        }

        TextView txtitem = convertView.findViewById(R.id.title);
        ImageView image = convertView.findViewById(R.id.image);

        txtitem.setText(Items.get(position));

        // Check if the item is a folder or a file
        if (Items.get(position).endsWith("/")) {
            txtitem.setTextColor(Color.BLUE);
            image.setImageResource(R.drawable.folder_icon);
        } else {
            txtitem.setTextColor(Color.BLACK);
            image.setImageResource(R.drawable.file_icon);
        }

        // Change the background if the item is selected
        if (Selected.get(position)) {
            txtitem.setBackgroundColor(Color.RED);
        } else {
            txtitem.setBackgroundColor(Color.TRANSPARENT);
        }

        return convertView;
    }
}

