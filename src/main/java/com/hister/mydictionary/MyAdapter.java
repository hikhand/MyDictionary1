package com.hister.mydictionary;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

class MyAdapter extends ArrayAdapter<String> {
    private int selectedPos = -1;
    private ArrayList<Integer> selectedPoses;
    SharedPreferences myAdapterSP;

    public MyAdapter(Context context, int textViewResourceId, ArrayList<String> objects) {
        super(context, textViewResourceId, objects);
        selectedPoses = new ArrayList<Integer>();
    }

    public void setSelectedPosition(int pos) {
        selectedPos = pos;
        // inform the view of this change
        notifyDataSetChanged();
    }

    public int getSelectedPosition() {
        return selectedPos;
    }

    public void setSelectedPositions(int pos) {
        selectedPoses.add(pos);
        notifyDataSetChanged();
    }

    public ArrayList<Integer> getSelectedPositions() {
        return selectedPoses;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        // only inflate the view if it's null
        if (v == null) {
            LayoutInflater vi
                    = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.listview_row, null);
        }

        // get text view
        TextView label = (TextView) v.findViewById(R.id.text1);

        // change the row color based on selected state

        if (selectedPoses.size() > 0) {
            for (int i = 0; i < selectedPoses.size(); i++) {
                if (selectedPoses.get(i) == position) {
                    v.setBackgroundColor(Color.parseColor("#1aa3d2"));
                } else if (v.getDrawingCacheBackgroundColor() == Color.parseColor("#1aa3d2")) {
                } else {
                    v.setBackgroundColor(Color.parseColor("#ededed"));

                }
            }
        }



//        if (selectedPos == position) {
//            v.setBackgroundColor(Color.parseColor("#1aa3d2"));
//        } else {
//            v.setBackgroundColor(Color.parseColor("#ededed"));
//        }

        label.setText(this.getItem(position).toString());

	        /*
            // to use something other than .toString()
	        MyClass myobj = (MyClass)this.getItem(position);
	        label.setText(myobj.myReturnsString());
	        */

        return (v);
    }
}
