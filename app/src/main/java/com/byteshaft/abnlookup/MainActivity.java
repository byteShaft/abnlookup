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
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;


public class MainActivity extends AppCompatActivity {

    private EditText editText;
    private Button query;
    private AbnSearchWSHttpGet abnSearchWSHttpGet;
    private Toolbar toolbar;
    private MaterialSearchView materialSearchView;
    private String searchValue = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.editText);
        query = findViewById(R.id.button);
        toolbar = findViewById(R.id.toolbar);
        materialSearchView = findViewById(R.id.search);
        setSupportActionBar(toolbar);
        abnSearchWSHttpGet = new AbnSearchWSHttpGet();
        query.setOnClickListener(v -> {
            String query = editText.getText().toString();
            searchValue = String.valueOf(query);
            Log.i("TAG", "value " + searchValue);
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

        private static final int ABN = 0;
        private static final int ACN = 1;
        private static final int NAME = 2;
        private int MODE = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startActivity(new Intent(getApplicationContext(), LoadingActivity.class));
        }

        public boolean isAlpha(String name) {
            return name.matches("[a-zA-Z]+");
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            if (strings[0].matches("[0-9]+") && strings[0].length() > 2) {
                if (strings[0].length() == 9) {
                    try {
                        MODE = ACN;
                        return abnSearchWSHttpGet.searchByACN(strings[0], true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    MODE = ABN;
                    return abnSearchWSHttpGet.doQuery(strings[0], true);
                }

            } else if (isAlpha(strings[0])) {
                    try {
                        MODE = NAME;
                        return abnSearchWSHttpGet.searchByNameSimpleProtocol(strings[0], false, false, false, true, true,
                               true, true, true, true, true, "all");
                    } catch (URISyntaxException | IOException | SAXException | ParserConfigurationException e) {
                        e.printStackTrace();
                    }
            }
            return new JSONObject();
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            ArrayList<Serializer> serializerArrayList = new ArrayList<>();
            super.onPostExecute(jsonObject);
            Log.i("MODE", "mode " + MODE);
            switch (MODE) {
                case ABN:
                if (jsonObject != null) {
                    try {
                        JSONObject mainObject = jsonObject.getJSONObject("ABRPayloadSearchResults");
                        JSONObject response = mainObject.getJSONObject("response");
                        JSONObject businessEntity = response.getJSONObject("businessEntity201408");
                        // single objects
                        JSONObject entityStatusObject = businessEntity.getJSONObject("entityStatus");
                        JSONObject abn = businessEntity.getJSONObject("ABN");
                        String acn = businessEntity.getString("ASICNumber");

                        JSONArray physicalAddressArray = businessEntity.getJSONArray("mainBusinessPhysicalAddress");
                        JSONArray mainNameArray = businessEntity.getJSONArray("mainName");

                        for (int i = 0; i < physicalAddressArray.length(); i++) {
                            JSONObject addressObject = physicalAddressArray.getJSONObject(i);
                            Log.i("TAG", "single " + addressObject);

                            Serializer serializer = new Serializer();
                            // single objects
                            serializer.setEntityStatus(entityStatusObject.getString("entityStatusCode"));
                            serializer.setIdentifierValue(abn.getString("identifierValue"));
                            serializer.setAbnActive((abn.getString("isCurrentIndicator").equals("Y") ? true : false));
                            //
                            serializer.setACN(acn);

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

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
                case NAME:
                    if (jsonObject != null) {
                        Log.i("TAG", "json object " + jsonObject);
                        try {
                            JSONObject mainObject = jsonObject.getJSONObject("ABRPayloadSearchResults");
                            JSONObject response = mainObject.getJSONObject("response");
                            JSONObject mainList = response.getJSONObject("searchResultsList");
                            JSONArray jsonArray = mainList.getJSONArray("searchResultsRecord");
//                             single objects
//                            JSONObject entityStatusObject = businessEntity.getJSONObject("entityStatus");
//                            String acn = businessEntity.getString("ASICNumber");

//                            JSONArray physicalAddressArray = businessEntity.getJSONArray("mainBusinessPhysicalAddress");
//                            JSONArray mainNameArray = businessEntity.getJSONArray("searchResultsRecord");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject singleOrgdata = jsonArray.getJSONObject(i);
                                Log.i("TAG", "single main " + singleOrgdata);
                                if (singleOrgdata.has("legalName")) {
                                    JSONObject mainPhysicalAddress = singleOrgdata.getJSONObject("mainBusinessPhysicalAddress");
                                    JSONObject legal = singleOrgdata.getJSONObject("legalName");
                                    Serializer serializer = new Serializer();
                                    JSONObject abn = singleOrgdata.getJSONObject("ABN");
                                    
                                    // single objects
                                    serializer.setIdentifierValue(abn.getString("identifierValue"));
                                    serializer.setAbnActive((abn.getString("identifierStatus").equals("Active") ? true : false));
                                    //
                                    serializer.setPostcode(mainPhysicalAddress.getString("postcode"));
                                    serializer.setStateCode(mainPhysicalAddress.optString("stateCode"));
                                    serializer.setCurrentIndicator(legal.getString("isCurrentIndicator").equals("Y") ? true : false);
                                    serializer.setOrganisationName(legal.getString("fullName"));
                                    serializerArrayList.add(serializer);
                                } else {
                                    JSONObject mainPhysicalAddress = singleOrgdata.getJSONObject("mainBusinessPhysicalAddress");
                                    JSONObject main = null;
                                    if (singleOrgdata.has("mainTradingName")) {
                                        main = singleOrgdata.getJSONObject("mainTradingName");
                                    } else if (singleOrgdata.has("mainName")) {
                                        main = singleOrgdata.getJSONObject("mainName");
                                    } else if (singleOrgdata.has("businessName")) {
                                        main = singleOrgdata.getJSONObject("businessName");
                                    } else if (singleOrgdata.has("otherTradingName")) {
                                        main = singleOrgdata.getJSONObject("otherTradingName");
                                    }
                                    Serializer serializer = new Serializer();
                                    JSONObject abn = singleOrgdata.getJSONObject("ABN");

                                    // single objects
                                    serializer.setIdentifierValue(abn.getString("identifierValue"));
                                    serializer.setAbnActive((abn.getString("identifierStatus").equals("Y") ? true : false));
                                    //
                                    serializer.setPostcode(mainPhysicalAddress.getString("postcode"));
                                    serializer.setStateCode(mainPhysicalAddress.getString("stateCode"));

                                    serializer.setOrganisationName(main.getString("organisationName"));
                                    serializerArrayList.add(serializer);
                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    break;
                case ACN:
                    if (jsonObject != null) {
                        try {
                            Log.i("TAG", "JsonObject " + jsonObject);
                            JSONObject mainObject = jsonObject.getJSONObject("ABRPayloadSearchResults");
                            JSONObject response = mainObject.getJSONObject("response");
                            JSONObject businessEntity = response.getJSONObject("businessEntity");
                            String acn = businessEntity.getString("ASICNumber");

                            // single objects
                            JSONObject entityStatusObject = businessEntity.getJSONObject("entityStatus");
                            JSONObject abn = businessEntity.getJSONObject("ABN");



                            JSONArray physicalAddressArray = businessEntity.getJSONArray("mainBusinessPhysicalAddress");
                            JSONArray mainNameArray = businessEntity.getJSONArray("mainName");

                            for (int i = 0; i < physicalAddressArray.length(); i++) {
                                JSONObject addressObject = physicalAddressArray.getJSONObject(i);
                                Serializer serializer = new Serializer();

                                // single objects
                                serializer.setACN(acn);
                                serializer.setAbnFrom(abn.getString("replacedFrom"));
                                serializer.setAbnActive((abn.getString("isCurrentIndicator").equals("Y") ? true : false));
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

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
            if (LoadingActivity.getInstance() != null) {
                LoadingActivity.getInstance().finish();
            }
            Intent intent = new Intent(MainActivity.this, ActivityLookup.class);
            intent.putExtra("search_value", searchValue);
            intent.putExtra("list", serializerArrayList);
            startActivity(intent);

        }
    }
}
