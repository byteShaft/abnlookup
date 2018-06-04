package com.byteshaft.abnlookup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ActivityLookup extends AppCompatActivity {

    private ListView listView;
    private ArrayList<Serializer> items;
    private ListAdapter adapter;
    private EditText searchView;
    private String query;
    private int mode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_lookup);
        mode = getIntent().getIntExtra("mode", 0);
        query = getIntent().getStringExtra("search_value");
        Log.i("TAG", "received " + query);
        items = (ArrayList<Serializer>) getIntent().getSerializableExtra("list");
        listView = findViewById(R.id.list_view);
        searchView = findViewById(R.id.search_view);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Serializer serializable = items.get(position);
            if (mode == AppGlobals.NAME) {
                Intent intent = new Intent(getApplicationContext(), LoadingActivity.class);
                intent.putExtra("query", serializable.identifierValue);
                intent.putExtra("mode", AppGlobals.NAME_DETAIL);
                startActivity(intent);
            } else {
                Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                intent.putExtra("mode", mode);
                intent.putExtra("abn", serializable.getIdentifierValue());
                if (serializable.getACN() != null) {
                    intent.putExtra("acn", serializable.getACN());
                } else {
                    intent.putExtra("acn", "empty");
                }
                intent.putExtra("abn_status", serializable.isAbnActive());
                intent.putExtra("abn_from", serializable.getAbnFrom());
                intent.putExtra("entities", items);
                intent.putExtra("entity_type", serializable.getEntityType());
                startActivity(intent);
            }
        });
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
        private ArrayList<Serializer> listItems;

        public ListAdapter(Context context, ArrayList<Serializer> listItems) {
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
            Serializer singleObject = listItems.get(position);
            viewHolder.title.setText(singleObject.getOrganisationName());
            viewHolder.stateCode.setText(singleObject.getStateCode());
            viewHolder.postCode.setText(singleObject.getPostcode());
            viewHolder.entityName.setText(singleObject.getOrgTitle());
            //single objects
            if (singleObject.getEntityStatus() != null) {
                viewHolder.status.setText(singleObject.getEntityStatus());
            } else {
                if (singleObject.isAbnActive()) {
                    viewHolder.status.setText("Active");
                    viewHolder.status.setTextColor(getResources().getColor(android.R.color.holo_green_light));
                } else {
                    viewHolder.status.setText("Cancelled");
                    viewHolder.status.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                }
            }
            if (singleObject.identifierValue != null && !singleObject.identifierValue.isEmpty()) {
                viewHolder.identifierValue.setText(String.valueOf(singleObject.identifierValue));
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
