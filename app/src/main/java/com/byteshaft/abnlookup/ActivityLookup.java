package com.byteshaft.abnlookup;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class ActivityLookup extends AppCompatActivity {

    private ListView listView;
    private List<Serializer> items;
    private ListAdapter adapter;
    private android.support.v7.widget.SearchView searchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lookup);
        items = (List<Serializer>) getIntent().getSerializableExtra("list");
        listView = findViewById(R.id.list_view);
        searchView = findViewById(R.id.search_view);
        adapter = new ListAdapter(getApplicationContext(), items);
        listView.setAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // TODO: 25/05/2018 addd search query here 
                return true;
            }
        });

    }

    private class ListAdapter extends BaseAdapter {
        private ViewHolder viewHolder;
        private Context context;
        private List<Serializer> listItems;

        public ListAdapter(Context context, List<Serializer> listItems) {
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
                viewHolder.identifierValue = convertView.findViewById(R.id.text_view_identifier_value);
                viewHolder.stateCode = convertView.findViewById(R.id.text_view_state_code);
                viewHolder.status = convertView.findViewById(R.id.text_view_state);
                viewHolder.postCode = convertView.findViewById(R.id.text_view_postcode);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            Serializer items = listItems.get(position);
            viewHolder.title.setText(items.getOrganisationName());
            viewHolder.stateCode.setText(items.getStateCode());
            viewHolder.postCode.setText(items.getPostcode());
            //single objects
            viewHolder.status.setText(items.getEntityStatus());
            viewHolder.identifierValue.setText(items.identifierValue);
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
            TextView identifierValue;
            TextView status;
            TextView stateCode;
            TextView postCode;
        }
    }

}
