package com.sergey.adcontacts.com.sergey.adcontacts.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.sergey.adcontacts.R;

import java.util.HashMap;
import java.util.List;

public class ContactsAdapter extends SimpleAdapter {

    Context context;
    List<HashMap<String, String>> data;
    int resource;
    String[] from;
    int[] to;

    /**
     * Constructor
     *
     * @param context  The context where the View associated with this SimpleAdapter is running
     * @param data     A List of Maps. Each entry in the List corresponds to one row in the list. The
     *                 Maps contain the data for each row, and should include all the entries specified in
     *                 "from"
     * @param resource Resource identifier of a view layout that defines the views for this list
     *                 item. The layout file should include at least those named views defined in "to"
     * @param from     A list of column names that will be added to the Map associated with each
     *                 item.
     * @param to       The views that should display column in the "from" parameter. These should all be
     *                 TextViews. The first N views in this list are given the values of the first N columns
     */
    public ContactsAdapter(Context context, List<HashMap<String, String>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        this.context = context;
        this.data = data;
        this.resource = resource;
        this.from = from;
        this.to = to;
    }

    static class ViewHolder {
        TextView nameCell;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(resource, parent, false);
        TextView NAME_CELL = (TextView) rowView.findViewById(R.id.CONTACTS_NAME_CELL);
        HashMap<String, String> mapValue = data.get(position);
        NAME_CELL.setText(mapValue.get("FIO"));
        return rowView;
    }
}
