package com.byteshaft.abnlookup;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class ActivityLookup extends AppCompatActivity {

    private ListView listView;
    private List<Serializer> items;
    private ListAdapter adapter;
    private EditText searchView;
    private String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_lookup);
        query = getIntent().getStringExtra("search_value");
        Log.i("TAG", "received " + query);
        items = (List<Serializer>) getIntent().getSerializableExtra("list");
        listView = findViewById(R.id.list_view);
        searchView = findViewById(R.id.search_view);
        adapter = new ListAdapter(getApplicationContext(), items);
        listView.setAdapter(adapter);
        searchView.setText(query);
        searchView.setEnabled(false);
        searchView.clearFocus();
        if (searchView != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
                default: return false;
        }
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
            Log.i("ADAPTER ", " identifier value " + items.identifierValue);
            if (items.getEntityStatus() != null) {
                viewHolder.status.setText(items.getEntityStatus());
            } else {
                if (items.isAbnActive()) {
                    viewHolder.status.setText("Active");
                    viewHolder.status.setTextColor(getResources().getColor(android.R.color.holo_green_light));
                } else {
                    viewHolder.status.setText("Cancelled");
                    viewHolder.status.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                }
            }
            if (items.identifierValue != null && !items.identifierValue.isEmpty()) {
                viewHolder.identifierValue.setText(String.valueOf(items.identifierValue));
            } else {
                viewHolder.identifierValue.setText("");
            }
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
