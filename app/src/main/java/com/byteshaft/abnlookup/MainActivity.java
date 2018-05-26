package com.byteshaft.abnlookup;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editText;
    private TextView postCode;
    private Button query;

    private AbnSearchWSHttpGet abnSearchWSHttpGet;
    private String searchValue = "";
    private MultiSelectionSpinner spinner;
    private List<String> dataset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        postCode = findViewById(R.id.text_view_postcode);
        editText = findViewById(R.id.editText);
        spinner = findViewById(R.id.spinner);
        query = findViewById(R.id.button);
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
        query.setOnClickListener(this);
        postCode.setOnClickListener(this);
    }

    private void addPostCode() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Enter Postcode");
        alertDialog.setMessage("");
        FrameLayout container = new FrameLayout(MainActivity.this);
        EditText input = (EditText) getLayoutInflater().inflate(R.layout.dialog_editext, null);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
        lp.rightMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
        lp.bottomMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
        container.addView(input);
        input.setLayoutParams(lp);
        alertDialog.setView(container);
        alertDialog.setPositiveButton(getString(R.string.set),
                (dialog, which) -> {
                    String wishlist = input.getText().toString();
                    postCode.setText(wishlist);
                });
        alertDialog.setNegativeButton(getString(R.string.cancel),
                (dialog, which) -> dialog.cancel());
        alertDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem item = menu.findItem(R.id.action_search);

        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.text_view_postcode:
                addPostCode();
                break;
            case R.id.button:
                Log.wtf("Items ", spinner.getSelectedItemsAsString());
                String query = editText.getText().toString();
                searchValue = String.valueOf(query);
                new Query().execute(query);
                break;
        }
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
            if (LoadingActivity.getInstance() != null) {
                LoadingActivity.getInstance().finish();
                overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);

            }
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

            Intent intent = new Intent(MainActivity.this, ActivityLookup.class);
            intent.putExtra("search_value", searchValue);
            intent.putExtra("list", serializerArrayList);
            startActivity(intent);
            overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right);

        }
    }
}
