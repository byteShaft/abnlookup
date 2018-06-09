package com.byteshaft.abnlookup;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.github.ybq.android.spinkit.SpinKitView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import static com.byteshaft.abnlookup.AppGlobals.ABN;
import static com.byteshaft.abnlookup.AppGlobals.ACN;
import static com.byteshaft.abnlookup.AppGlobals.MODE;

public class LoadingActivity extends Activity {

    private static LoadingActivity instance;
    private SpinKitView spinKitView;
    private String searchValue = "";
    private AbnSearchWSHttpGet abnSearchWSHttpGet;
    private int mode;
    private String nameTYpe;
    private String postCode;

    public static LoadingActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.loading_screen);
        abnSearchWSHttpGet = new AbnSearchWSHttpGet(this);
        searchValue = getIntent().getStringExtra("query");
        mode = getIntent().getIntExtra("mode", 0);
        spinKitView = findViewById(R.id.spin_kit);
        spinKitView.setIndeterminate(true);
        instance = this;
        new Query().execute(searchValue);
    }


    private class Query extends AsyncTask<String, String, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        public boolean isAlpha(String name) {
            return name.matches("[a-zA-Z]+");
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            if (strings[0].matches("[0-9]+")) {
                if (strings[0].length() == 9) {
                    try {
                        return abnSearchWSHttpGet.searchByACN(strings[0], true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    return abnSearchWSHttpGet.doQuery(strings[0], true);
                }

            } else {
                boolean allName = MainActivity.selectedNameType.get("All");
                boolean entityName = MainActivity.selectedNameType.get("Entity Name");
                boolean businessName = MainActivity.selectedNameType.get("Business Name");
                boolean tradingName = MainActivity.selectedNameType.get("Trading Name");
                if (allName) {
                    entityName = true;
                    businessName = true;
                    tradingName = true;
                }

                boolean act = MainActivity.selectedStates.get("ACT");
                boolean nsw = MainActivity.selectedStates.get("NSW");
                boolean nt = MainActivity.selectedStates.get("NT");
                boolean qld = MainActivity.selectedStates.get("QLD");
                boolean sa = MainActivity.selectedStates.get("SA");
                boolean tas = MainActivity.selectedStates.get("TAS");
                boolean vic = MainActivity.selectedStates.get("VIC");
                boolean wa = MainActivity.selectedStates.get("WA");
                boolean allStates = false;

                for (Map.Entry<String, Boolean> entry : MainActivity.selectedStates.entrySet()) {
                    String key = entry.getKey();
                    Boolean value = entry.getValue();
                    if (value) {
                        allStates = true;
                    }
                    // do stuff
                }
                try {
                    return abnSearchWSHttpGet.searchByNameSimpleProtocol(strings[0], allName,
                            entityName, businessName, tradingName, act, nsw, nt, qld, sa,
                            tas, vic, wa, allStates, MainActivity.postCodeValue);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return new JSONObject();
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            ArrayList<Serializer> serializerArrayList = new ArrayList<>();
            super.onPostExecute(jsonObject);
            switch (mode) {
                case ABN:
                    if (jsonObject != null) {
                        try {
                            JSONObject mainObject = jsonObject.getJSONObject("ABRPayloadSearchResults");
                            JSONObject response = mainObject.getJSONObject("response");
                            JSONObject businessEntity = response.getJSONObject("businessEntity201408");
                            // single objects

                            Serializer serializer = new Serializer();

                            Object entityStatusObject = businessEntity.get("entityStatus");
                            if (entityStatusObject instanceof JSONObject) {
                                serializer.setEntityStatus(((JSONObject) entityStatusObject).getString("entityStatusCode"));
                            } else if (entityStatusObject instanceof JSONArray) {
                                JSONObject jsonObject1 = ((JSONArray) entityStatusObject).getJSONObject(0);
                                serializer.setEntityStatus(jsonObject1.getString("entityStatusCode"));
                            }

//                            JSONObject entityStatusObject = businessEntity.getJSONObject("entityStatus");
                            JSONObject abn = businessEntity.getJSONObject("ABN");
                            String acn = businessEntity.getString("ASICNumber");

                            Object physicalAddressArray = businessEntity.get("mainBusinessPhysicalAddress");
                            Object mainNameArray = null;
                            if (businessEntity.has("mainName")) {
                                mainNameArray = businessEntity.get("mainName");
                            } else if (businessEntity.has("legalName")) {
                                mainNameArray = businessEntity.get("legalName");
                            }
                            if (physicalAddressArray instanceof JSONArray) {
                                for (int i = 0; i < ((JSONArray) physicalAddressArray).length(); i++) {
                                    JSONObject addressObject = ((JSONArray) physicalAddressArray)
                                            .getJSONObject(i);
                                    if (businessEntity.has("mainName")) {
                                        if (mainNameArray instanceof JSONArray) {
                                            Log.i("TAG", "array " + ((JSONArray) mainNameArray));
                                            JSONObject mainNameObject = ((JSONArray) mainNameArray).getJSONObject(i);
                                            serializer.setOrganisationName(mainNameObject.getString("organisationName"));
                                            serializer.setMainNameEffectiveFrom(mainNameObject.getString("effectiveFrom"));
                                            if (mainNameObject.has("effectiveTo")) {
                                                serializer.setMainNameEffectiveTo(mainNameObject.getString("effectiveTo"));
                                            }
                                        } else if (mainNameArray instanceof JSONObject) {
                                            if (((JSONObject) mainNameArray).has("givenName")) {
                                                serializer.setOrganisationName(((JSONObject) mainNameArray).getString("givenName") +
                                                        (((JSONObject) mainNameArray).getString("familyName")));
                                            } else if (((JSONObject) mainNameArray).has("organisationName")) {
                                                serializer.setOrganisationName(((JSONObject) mainNameArray)
                                                        .getString("organisationName"));
                                            }
                                            Log.i("TAG", "OBJ" + serializer.getOrganisationName());
                                            serializer.setMainNameEffectiveFrom(((JSONObject) mainNameArray).getString("effectiveFrom"));
                                            if (((JSONObject) mainNameArray).has("effectiveTo")) {
                                                serializer.setMainNameEffectiveTo(((JSONObject) mainNameArray).getString("effectiveTo"));
                                            }
                                        }
                                    } else if (businessEntity.has("legalName")) {
                                        if (mainNameArray instanceof JSONObject) {
                                            serializer.setOrganisationName(((JSONObject) mainNameArray).getString("givenName") + " ," +
                                                    ((JSONObject) mainNameArray).getString("familyName"));
                                            serializer.setMainNameEffectiveFrom(((JSONObject) mainNameArray).getString("effectiveFrom"));
                                            if (((JSONObject) mainNameArray).has("effectiveTo")) {
                                                serializer.setMainNameEffectiveTo(((JSONObject) mainNameArray).getString("effectiveTo"));
                                            }

                                        }
                                    }
                                    // single objects
                                    // entity type
                                    JSONObject entityType = businessEntity.getJSONObject("entityType");
                                    serializer.setEntityType(entityType.getString("entityDescription"));

//                                    serializer.setEntityStatus(entityStatusObject.getString("entityStatusCode"));
                                    serializer.setIdentifierValue(abn.getString("identifierValue"));
                                    serializer.setAbnFrom(abn.getString("replacedFrom"));
                                    serializer.setAbnActive((abn.getString("isCurrentIndicator").equals("Y") ? true : false));
                                    //
                                    serializer.setACN(acn);

                                    serializer.setEffectiveTo(addressObject.getString("effectiveTo"));
                                    serializer.setEffectiveFrom(addressObject.getString("effectiveFrom"));
                                    serializer.setPostcode(addressObject.getString("postcode"));
                                    serializer.setStateCode(addressObject.getString("stateCode"));
                                    serializerArrayList.add(serializer);
                                }
                            } else if (physicalAddressArray instanceof JSONObject) {
                                if (((JSONObject) mainNameArray).has("mainTradingName")) {
                                    serializer.setOrgTitle("Trading Name");
                                } else if (((JSONObject) mainNameArray).has("mainName")) {
                                } else if (((JSONObject) mainNameArray).has("businessName")) {
                                    serializer.setOrgTitle("Business Name");
                                }

                                if (businessEntity.has("mainName")) {
                                    if (mainNameArray instanceof JSONArray) {
                                        for (int i = 0; i < ((JSONArray) mainNameArray).length(); i++) {
                                            JSONObject mainNameObject = ((JSONArray) mainNameArray).getJSONObject(i);
                                            serializer.setOrganisationName(mainNameObject.getString("organisationName"));
                                            serializer.setMainNameEffectiveFrom(mainNameObject.getString("effectiveFrom"));
                                            if (mainNameObject.has("effectiveTo")) {
                                                serializer.setMainNameEffectiveTo(mainNameObject.getString("effectiveTo"));
                                            }
                                        }
                                    } else if (mainNameArray instanceof JSONObject) {
                                        if (((JSONObject) mainNameArray).has("givenName")) {
                                            serializer.setOrganisationName(((JSONObject) mainNameArray).getString("givenName") +
                                                    (((JSONObject) mainNameArray).getString("familyName")));
                                        } else if (((JSONObject) mainNameArray).has("organisationName")) {
                                            serializer.setOrganisationName(((JSONObject) mainNameArray).getString("organisationName"));
                                        }
                                        serializer.setMainNameEffectiveFrom(((JSONObject) mainNameArray).getString("effectiveFrom"));
                                        if (((JSONObject) mainNameArray).has("effectiveTo")) {
                                            serializer.setMainNameEffectiveTo(((JSONObject) mainNameArray).getString("effectiveTo"));
                                        }
                                    }
                                } else if (businessEntity.has("legalName")) {
                                    if (mainNameArray instanceof JSONObject) {
                                        serializer.setOrganisationName(((JSONObject) mainNameArray).getString("givenName") + " ," +
                                                ((JSONObject) mainNameArray).getString("familyName"));
                                        serializer.setMainNameEffectiveFrom(((JSONObject) mainNameArray).getString("effectiveFrom"));
                                        if (((JSONObject) mainNameArray).has("effectiveTo")) {
                                            serializer.setMainNameEffectiveTo(((JSONObject) mainNameArray).getString("effectiveTo"));
                                        }

                                    }
                                }
                                // single objects
                                // entity type
                                JSONObject entityType = businessEntity.getJSONObject("entityType");
                                serializer.setEntityType(entityType.getString("entityDescription"));

                                serializer.setIdentifierValue(abn.getString("identifierValue"));
                                serializer.setAbnFrom(abn.getString("replacedFrom"));
                                serializer.setAbnActive((abn.getString("isCurrentIndicator").equals("Y") ? true : false));
                                //
                                serializer.setACN(acn);

                                serializer.setEffectiveTo(((JSONObject) physicalAddressArray).getString("effectiveTo"));
                                serializer.setEffectiveFrom(((JSONObject) physicalAddressArray).getString("effectiveFrom"));
                                serializer.setPostcode(((JSONObject) physicalAddressArray).getString("postcode"));
                                serializer.setStateCode(((JSONObject) physicalAddressArray).getString("stateCode"));
                                serializerArrayList.add(serializer);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case AppGlobals.NAME:
                    if (jsonObject != null) {
                        Log.i("TAG", "json object " + jsonObject);
                        try {
                            JSONObject mainObject = jsonObject.getJSONObject("ABRPayloadSearchResults");
                            JSONObject response = mainObject.getJSONObject("response");
                            JSONObject mainList = response.getJSONObject("searchResultsList");
                            JSONArray jsonArray = mainList.getJSONArray("searchResultsRecord");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject singleOrgdata = jsonArray.getJSONObject(i);
                                Serializer serializer = new Serializer();
                                Log.i("TAG", "single main " + singleOrgdata);
                                if (singleOrgdata.has("legalName")) {
                                    JSONObject mainPhysicalAddress = singleOrgdata.getJSONObject("mainBusinessPhysicalAddress");
                                    JSONObject legal = singleOrgdata.getJSONObject("legalName");
                                    JSONObject abn = singleOrgdata.getJSONObject("ABN");

                                    // single objects
                                    serializer.setIdentifierValue(abn.getString("identifierValue"));
                                    serializer.setAbnFrom(abn.optString("replacedFrom"));
                                    serializer.setAbnActive((abn.getString("identifierStatus").equals("Active") ? true : false));
                                    //
                                    serializer.setPostcode(mainPhysicalAddress.getString("postcode"));
                                    serializer.setStateCode(mainPhysicalAddress.optString("stateCode"));
                                    serializer.setCurrentIndicator(legal.getString("isCurrentIndicator").equals("Y") ? true : false);
                                    serializer.setOrganisationName(legal.getString("fullName"));
                                    serializer.setMainNameEffectiveFrom(legal.optString("effectiveFrom"));

                                    serializerArrayList.add(serializer);
                                } else {
                                    JSONObject mainPhysicalAddress = singleOrgdata.getJSONObject("mainBusinessPhysicalAddress");
                                    JSONObject main = null;
                                    if (singleOrgdata.has("mainTradingName")) {
                                        main = singleOrgdata.getJSONObject("mainTradingName");
                                        serializer.setOrgTitle("Trading Name");
                                    } else if (singleOrgdata.has("mainName")) {
                                        main = singleOrgdata.getJSONObject("mainName");
                                    } else if (singleOrgdata.has("businessName")) {
                                        main = singleOrgdata.getJSONObject("businessName");
                                        serializer.setOrgTitle("Business Name");
                                    } else if (singleOrgdata.has("otherTradingName")) {
                                        main = singleOrgdata.getJSONObject("otherTradingName");
                                    }
                                    JSONObject abn = singleOrgdata.getJSONObject("ABN");

                                    // single objects
                                    serializer.setIdentifierValue(abn.getString("identifierValue"));
                                    serializer.setAbnFrom(abn.optString("replacedFrom"));
                                    serializer.setAbnActive((abn.getString("identifierStatus")
                                            .equals("Active") ? true : false));
                                    //
                                    serializer.setPostcode(mainPhysicalAddress.getString("postcode"));
                                    serializer.setStateCode(mainPhysicalAddress.getString("stateCode"));

                                    serializer.setOrganisationName(main.getString("organisationName"));
                                    serializer.setMainNameEffectiveFrom(main.optString("effectiveFrom"));
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



                            Object physicalAddressArray = businessEntity.get("mainBusinessPhysicalAddress");

                            if (physicalAddressArray instanceof JSONArray) {
                                Object mainNameArray = businessEntity.get("mainName");
                                for (int i = 0; i < ((JSONArray)physicalAddressArray).length(); i++) {
                                    JSONObject addressObject = ((JSONArray)physicalAddressArray).getJSONObject(i);
                                    Serializer serializer = new Serializer();

                                    // single objects
                                    serializer.setACN(acn);
                                    serializer.setAbnFrom(abn.getString("replacedFrom"));
                                    serializer.setAbnActive((abn.getString("isCurrentIndicator").equals("Y") ? true : false));
                                    serializer.setEntityStatus(entityStatusObject.getString("entityStatusCode"));
                                    serializer.setIdentifierValue(abn.getString("identifierValue"));

                                    serializer.setEffectiveTo(addressObject.getString("effectiveTo"));
                                    serializer.setEffectiveFrom(addressObject.getString("effectiveFrom"));
                                    serializer.setPostcode(addressObject.getString("postcode"));
                                    serializer.setStateCode(addressObject.getString("stateCode"));

                                    if (mainNameArray instanceof JSONArray) {
                                        JSONObject mainNameObject = ((JSONArray) mainNameArray).getJSONObject(i);
                                        serializer.setOrganisationName(mainNameObject.getString("organisationName"));
                                        serializer.setMainNameEffectiveFrom(mainNameObject.optString("effectiveFrom"));
                                        if (mainNameObject.has("effectiveTo")) {
                                            serializer.setMainNameEffectiveTo(mainNameObject.getString("effectiveTo"));
                                        }
                                    } else if (mainNameArray instanceof JSONObject) {
                                        serializer.setOrganisationName(((JSONObject) mainNameArray).getString("organisationName"));
                                        serializer.setMainNameEffectiveFrom(((JSONObject) mainNameArray).optString("effectiveFrom"));
                                        if (((JSONObject) mainNameArray).has("effectiveTo")) {
                                            serializer.setMainNameEffectiveTo(((JSONObject) mainNameArray).getString("effectiveTo"));
                                        }
                                    }
                                    serializerArrayList.add(serializer);
                                }
                            } else if (physicalAddressArray instanceof JSONObject) {
                                Object mainNameArray = businessEntity.get("mainName");
                                    Serializer serializer = new Serializer();

                                    // single objects
                                    serializer.setACN(acn);
                                    serializer.setAbnFrom(abn.getString("replacedFrom"));
                                    serializer.setAbnActive((abn.getString("isCurrentIndicator").equals("Y") ? true : false));
                                    serializer.setEntityStatus(entityStatusObject.getString("entityStatusCode"));
                                    serializer.setIdentifierValue(abn.getString("identifierValue"));

                                    serializer.setEffectiveTo(((JSONObject)physicalAddressArray).getString("effectiveTo"));
                                    serializer.setEffectiveFrom(((JSONObject)physicalAddressArray).getString("effectiveFrom"));
                                    serializer.setPostcode(((JSONObject)physicalAddressArray).getString("postcode"));
                                    serializer.setStateCode(((JSONObject)physicalAddressArray).getString("stateCode"));

                                    if (mainNameArray instanceof JSONArray) {
                                        for (int i = 0; i < ((JSONArray) mainNameArray).length() ; i++) {
                                            JSONObject mainNameObject = ((JSONArray) mainNameArray).getJSONObject(i);
                                            serializer.setOrganisationName(mainNameObject.getString("organisationName"));
                                            serializer.setMainNameEffectiveFrom(mainNameObject.optString("effectiveFrom"));
                                            if (mainNameObject.has("effectiveTo")) {
                                                serializer.setMainNameEffectiveTo(mainNameObject.getString("effectiveTo"));
                                            }
                                        }
                                    } else if (mainNameArray instanceof JSONObject) {
                                        serializer.setOrganisationName(((JSONObject) mainNameArray).getString("organisationName"));
                                        serializer.setMainNameEffectiveFrom(((JSONObject) mainNameArray).optString("effectiveFrom"));
                                        if (((JSONObject) mainNameArray).has("effectiveTo")) {
                                            serializer.setMainNameEffectiveTo(((JSONObject) mainNameArray).getString("effectiveTo"));
                                        }
                                    }
                                    serializerArrayList.add(serializer);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case AppGlobals.NAME_DETAIL:
                    try {
                        NameDetailSerializer nameDetail = new NameDetailSerializer();
                        JSONObject mainObject = jsonObject.getJSONObject("ABRPayloadSearchResults");
                        JSONObject response = mainObject.getJSONObject("response");
                        JSONObject businessEntity = response.getJSONObject("businessEntity201408");
                        Object mainPhysicalAddress = businessEntity.get("mainBusinessPhysicalAddress");

                        if (mainPhysicalAddress instanceof JSONArray) {
                            JSONObject physicalObject = ((JSONArray) mainPhysicalAddress).getJSONObject(0);
                            nameDetail.setBusinessLocation(physicalObject.optString("stateCode") + " "
                                    + physicalObject.optString("postcode"));
                        } else if (mainPhysicalAddress instanceof JSONObject) {
                            nameDetail.setBusinessLocation(((JSONObject) mainPhysicalAddress).optString("stateCode") + " "
                                    + ((JSONObject) mainPhysicalAddress).optString("postcode"));
                        }
                        if (businessEntity.has("goodsAndServicesTax")) {
                            Object gstMain = businessEntity.get("goodsAndServicesTax");
                            if (gstMain instanceof JSONObject) {
                                String gstDate = ((JSONObject) gstMain).getString("effectiveFrom");
                                nameDetail.setGst(gstDate);
                            } else if (gstMain instanceof JSONArray) {
                                JSONObject json = ((JSONArray) gstMain).getJSONObject(0);
                                String gstDate = json.getString("effectiveFrom");
                                nameDetail.setGst(gstDate);
                            }
                        }
                        if (businessEntity.has("businessName")) {
                            nameDetail.setOrgTitle("Business Name");
                            Object businessNames = businessEntity.get("businessName");
                            if (businessNames instanceof JSONArray) {
                                ArrayList<BusinessName> names = new ArrayList<>();
                                for (int i = 0; i < ((JSONArray) businessNames).length(); i++) {
                                    BusinessName business = new BusinessName();
                                    JSONObject businessName = ((JSONArray) businessNames).getJSONObject(i);
                                    business.setName(businessName.optString("organisationName"));
                                    business.setFrom(businessName.optString("effectiveFrom"));
                                    names.add(business);
                                }
                                nameDetail.setBusinessNames(names);
                            } else if (businessNames instanceof JSONObject) {
                                ArrayList<BusinessName> businessJson = new ArrayList<>();
                                BusinessName businessName = new BusinessName();
                                businessName.setName(((JSONObject) businessNames).optString("organisationName"));
                                businessName.setFrom(((JSONObject) businessNames).optString("effectiveFrom"));
                                businessJson.add(businessName);
                                nameDetail.setBusinessNames(businessJson);

                            }
                        }

                        if (businessEntity.has("mainTradingName")) {
                            nameDetail.setOrgTitle("Trading Name");
                            Object mainTradingName = businessEntity.get("mainTradingName");
                            if (mainTradingName instanceof JSONObject) {
                                nameDetail.setTradingName(((JSONObject) mainTradingName).optString("organisationName"));
                            } else if (mainTradingName instanceof JSONArray) {
                                JSONObject object = ((JSONArray) mainTradingName).getJSONObject(0);
                                nameDetail.setOrgTitle("Organization Name");
                                nameDetail.setTradingName(object.optString("organisationName"));
                            }

                        }

                        Object object = businessEntity.get("entityStatus");
                        if (object instanceof JSONArray) {
                            JSONArray abn = businessEntity.getJSONArray("entityStatus");
                            JSONObject abnDetail = abn.getJSONObject(0);
                            String status = abnDetail.optString("entityStatusCode");
                            String effectiveFrom = abnDetail.optString("effectiveFrom");

                            JSONObject entityType = businessEntity.getJSONObject("entityType");
                            nameDetail.setAbnStatus(status + " From " + effectiveFrom);
                            nameDetail.setEntityType(entityType.optString("entityDescription"));
                            Log.i("TAG", "entity type JsonArray" + entityType);

                        } else if (object instanceof JSONObject) {
                            JSONObject abn = businessEntity.getJSONObject("entityStatus");
                            String status = abn.optString("entityStatusCode");
                            String effectiveFrom = abn.optString("effectiveFrom");

                            JSONObject entityType = businessEntity.getJSONObject("entityType");
                            nameDetail.setAbnStatus(status + "From " + effectiveFrom);
                            nameDetail.setEntityType(entityType.optString("entityDescription"));

                            Log.i("TAG", "entity type JsonObject" + entityType);
                        }
                        if (businessEntity.has("mainName")) {
                            Object mainNameObject = businessEntity.get("mainName");
                            if (mainNameObject instanceof JSONObject) {
                                JSONObject nameObject = businessEntity.getJSONObject("mainName");
                                nameDetail.setEntityName(nameObject.optString("organisationName"));
                                nameDetail.setOrgTitle("Organization Name");
                            } else if (mainNameObject instanceof JSONArray) {
                                JSONObject obj = ((JSONArray) mainNameObject).getJSONObject(0);
                                nameDetail.setEntityName(obj.optString("organisationName"));
                                nameDetail.setOrgTitle("Organization Name");
                            }
                        } else {
                            Object obj = businessEntity.get("legalName");
                            if (obj instanceof JSONArray) {
                                JSONObject legalJsonObject = ((JSONArray) obj).getJSONObject(0);
                                nameDetail.setEntityName(legalJsonObject.optString("familyName")
                                        + "," + legalJsonObject.optString("givenName"));
                                Log.i("Tag", " name detail " + nameDetail.getEntityName());
                            } else if (obj instanceof JSONObject) {
                                nameDetail.setEntityName(((JSONObject) obj).optString("familyName")
                                        + "," + ((JSONObject) obj).optString("givenName"));
                            }
                        }
                        nameDetail.setACN(businessEntity.optString("ASICNumber"));
                        nameDetail.setLastUpdated(response.optString("dateRegisterLastUpdated"));
                        Log.i("TAG", " date last reg " + nameDetail.getLastUpdated());
                        Intent intent = new Intent(getApplicationContext(), NameDetail.class);
                        intent.putExtra("data", nameDetail);
                        startActivity(intent);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;
            }
            LoadingActivity.getInstance().finish();
            if (mode != AppGlobals.NAME_DETAIL) {
                Intent intent = new Intent(getApplicationContext(), ActivityLookup.class);
                intent.putExtra("mode", AppGlobals.MODE);
                intent.putExtra("search_value", searchValue);
                intent.putExtra("list", serializerArrayList);
                startActivity(intent);
            } else {

            }
        }
    }
}
