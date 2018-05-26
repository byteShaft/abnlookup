package com.byteshaft.abnlookup;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import com.claudiodegio.msv.MaterialSearchView;
import com.claudiodegio.msv.OnSearchViewListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private EditText editText;
    private Button query;
    private AbnSearchWSHttpGet abnSearchWSHttpGet;
    private Toolbar toolbar;
    private MaterialSearchView materialSearchView;
    private MultiSelectionSpinner spinner;
    private List<String> dataset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.editText);
        spinner = findViewById(R.id.spinner);
        query = findViewById(R.id.button);
        toolbar = findViewById(R.id.toolbar);
        materialSearchView = findViewById(R.id.search);
        setSupportActionBar(toolbar);
        List<String> list = new ArrayList<>();
        list.add("ACT");
        list.add("NSW");
        list.add("QLD");
        list.add("SA");
        list.add("TAS");
        list.add("VIC");
        list.add("WA");
        spinner.setItems(list);
        spinner.setSelection(0);
        spinner.getSelectedItemsAsString();

        Log.wtf("Items ", spinner.getSelectedItemsAsString());
        abnSearchWSHttpGet = new AbnSearchWSHttpGet();
        query.setOnClickListener(v -> {
            String query = editText.getText().toString();
            new Query().execute(query);
        });
        materialSearchView.setOnSearchViewListener(new OnSearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {

            }

            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.i("TAg", s);
                return false;
            }

            @Override
            public void onQueryTextChange(String s) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        materialSearchView.setMenuItem(item);

        return true;
    }

    private class Query extends AsyncTask<String, String, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... strings) {
            return abnSearchWSHttpGet.doQuery(strings[0]);
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            ArrayList<Serializer> serializerArrayList = new ArrayList<>();
            super.onPostExecute(jsonObject);
            if (jsonObject != null) {
                try {
                    JSONObject mainObject = jsonObject.getJSONObject("ABRPayloadSearchResults");
                    JSONObject response = mainObject.getJSONObject("response");
                    JSONObject businessEntity = response.getJSONObject("businessEntity201408");

                    // single objects
                    JSONObject entityStatusObject = businessEntity.getJSONObject("entityStatus");
                    JSONObject abn = businessEntity.getJSONObject("ABN");

                    JSONArray physicalAddressArray = businessEntity.getJSONArray("mainBusinessPhysicalAddress");
                    JSONArray mainNameArray = businessEntity.getJSONArray("mainName");

                    for (int i = 0; i < physicalAddressArray.length(); i++) {
                        JSONObject addressObject = physicalAddressArray.getJSONObject(i);
                        Serializer serializer = new Serializer();

                        // single objects
                        serializer.setEntityStatus(entityStatusObject.getString("entityStatusCode"));
                        serializer.setIdentifierValue(abn.getString("identifierValue"));
                        //

                        serializer.setEffectiveTo(addressObject.getString("effectiveTo"));
                        serializer.setEffectiveFrom(addressObject.getString("effectiveFrom"));
                        serializer.setPostcode(addressObject.getString("postcode"));
                        serializer.setStateCode(addressObject.getString("stateCode"));

                        JSONObject mainNameObject = mainNameArray.getJSONObject(i);
                        serializer.setOrganisationName(mainNameObject.getString("organisationName"));
                        serializer.setMainNameEffectiveFrom(mainNameObject.getString("effectiveFrom"));
                        if (mainNameObject.has("effectiveTo")) {
                            serializer.setMainNameEffectiveTo(mainNameObject.getString("effectiveTo"));
                        }
                        serializerArrayList.add(serializer);
                    }
                    Intent intent = new Intent(MainActivity.this, ActivityLookup.class);
                    intent.putExtra("list", serializerArrayList);
                    startActivity(intent);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
    }
}
