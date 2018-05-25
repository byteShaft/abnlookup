package com.byteshaft.abnlookup;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ActivityLookup extends AppCompatActivity {

    private ListView listView;
    private List<String> items;
    private ListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lookup);
        items = new ArrayList<>();
        items.add("ok ");
        items.add("ok ");
        items.add("ok ");
        items.add("ok ");
        items.add("ok ");
        items.add("ok ");
        items.add("ok ");
        items.add("ok ");

        listView = findViewById(R.id.list_view);
        adapter = new ListAdapter(getApplicationContext(), items);
        listView.setAdapter(adapter);
    }

    private class ListAdapter extends BaseAdapter {
        private ViewHolder viewHolder;
        private Context context;
        /// change type accordingly
        private List<String> listItems;

        public ListAdapter(Context context, List<String> listItems) {
            this.context = context;
            this.listItems = listItems;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater()
                        .inflate(R.layout.delegate_lookup, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.title = convertView.findViewById(R.id.text_view_title);
                viewHolder.entityName = convertView.findViewById(R.id.text_view_entity_name);
                viewHolder.activeOrInactive = convertView.findViewById(R.id.text_view_active_or_inactive);
                viewHolder.cardNumber = convertView.findViewById(R.id.text_view_number);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            // TODO: 24/05/2018 set values here...
            return convertView;
        }

        @Override
        public int getCount() {
            return listItems.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        private class ViewHolder {
            TextView title;
            TextView entityName;
            TextView activeOrInactive;
            TextView cardNumber;
        }
    }

}
