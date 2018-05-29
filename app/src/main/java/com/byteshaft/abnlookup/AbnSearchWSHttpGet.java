package com.byteshaft.abnlookup;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import fr.arnaudguyon.xmltojsonlib.XmlToJson;

public class AbnSearchWSHttpGet {

    public AbnSearchWSHttpGet(Activity context) {
        this.context = context;
    }

    private String guid = "a1013045-797c-45d5-b573-8ee5526c69ec";
    private Activity context;

    private static final String UTF_8 = "UTF-8";

    public JSONObject doQuery(String query, boolean value) {
        JSONObject result = null;
        try {
            String abn = query;
            result = searchByABNv200506(abn, true);
            Log.i("TAG", " result  " + result);
        } catch (Exception e) {
            System.err.println("Caught exception : " + e);
            e.printStackTrace(System.err);
        }
        return result;
    }

    public JSONObject searchByABN(String abn, boolean includeHistorical) throws URISyntaxException, IOException,
            SAXException, ParserConfigurationException, FactoryConfigurationError {
        JSONObject results = null;

        String params = "";

        params += "&includeHistoricalDetails=" + encodeBooleanParam(includeHistorical);
        params += "&searchString=" + URLEncoder.encode(abn, UTF_8);

        results = doRequest("ABRSearchByABN", params);

        return results;
    }

    public JSONObject searchByACN(String acn, boolean includeHistorical) throws IOException
            , FactoryConfigurationError {
        JSONObject results = null;
        String params = "";
        params += "&includeHistoricalDetails=" + encodeBooleanParam(includeHistorical);
        params += "&searchString=" + URLEncoder.encode(acn, UTF_8);
        params += "&activeABNsOnly=Y";


        results = doRequest("ABRSearchByASIC", params);

        return results;
    }

    public JSONObject searchByABNv200506(String abn, boolean includeHistorical) throws URISyntaxException, IOException,
            FactoryConfigurationError {
        JSONObject results;

        String params = "";

        params += "&includeHistoricalDetails=" + encodeBooleanParam(includeHistorical);
        params += "&searchString=" + URLEncoder.encode(abn, UTF_8);
        params += "&activeABNsOnly=Y";


        results = doRequest("SearchByABNv201408", params);

        return results;
    }

    public JSONObject searchByACNv200506(String acn, boolean includeHistorical) throws URISyntaxException, IOException,
            SAXException, ParserConfigurationException, FactoryConfigurationError {
        JSONObject results;

        String params = "";

        params += "&includeHistoricalDetails=" + encodeBooleanParam(includeHistorical);
        params += "&searchString=" + URLEncoder.encode(acn, UTF_8);

        results = doRequest("SearchByASICv200506", params);

        return results;
    }

    public JSONObject searchByNameSimpleProtocol(String name, boolean allName, boolean entityName,
                                                 boolean businessName, boolean tradingName, boolean act, boolean nsw,
                                                 boolean nt, boolean qld, boolean sa, boolean tas,
                                                 boolean vic, boolean wa, boolean allStates,
                                                 String postcode) throws IOException,
            FactoryConfigurationError {
        JSONObject results;
        String params = "";
        params += "&name=" + URLEncoder.encode(name, UTF_8);
        params += "&legalName=" + encodeBooleanParam(true);
        params += "&AllNames=" + encodeBooleanParam(allName);
        params += "&EntityName=" + encodeBooleanParam(entityName);
        params += "&BusinessName=" + encodeBooleanParam(businessName);
        params += "&TradingName=" + encodeBooleanParam(tradingName);
        params += "&searchWidth=" + URLEncoder.encode("typical", UTF_8);
        params += "&minimumScore=" + URLEncoder.encode("50", UTF_8);
        params += "&maxSearchResults=" + URLEncoder.encode("200", UTF_8);


        params += "&AllStates=" + encodeBooleanParam(allStates);
        params += "&ACT=" + encodeBooleanParam(act);
        params += "&NSW=" + encodeBooleanParam(nsw);
        params += "&NT=" + encodeBooleanParam(nt);
        params += "&QLD=" + encodeBooleanParam(qld);
        params += "&SA=" + encodeBooleanParam(sa);
        params += "&TAS=" + encodeBooleanParam(tas);
        params += "&VIC=" + encodeBooleanParam(vic);
        params += "&WA=" + encodeBooleanParam(wa);
        params += "&activeABNsOnly=" + encodeBooleanParam(true);

        params += "&postcode=" + URLEncoder.encode(postcode, UTF_8);

        results = doRequest("ABRSearchByNameAdvancedSimpleProtocol2017", params);

        return results;
    }

    public JSONObject searchByNameAdvancedSimpleProtocol(String guid, String name, boolean legal, boolean trading, boolean act,
                                                         boolean nsw, boolean nt, boolean qld, boolean sa, boolean tas, boolean vic, boolean wa, String postcode, String width, int minScore)
            throws URISyntaxException, IOException, SAXException, ParserConfigurationException, FactoryConfigurationError {

        JSONObject results;
        String params = "";
        params += "&name=" + URLEncoder.encode(name, UTF_8);

        params += "&legalName=" + encodeBooleanParam(legal);
        params += "&tradingName=" + encodeBooleanParam(trading);

        params += "&ACT=" + encodeBooleanParam(act);
        params += "&NSW=" + encodeBooleanParam(nsw);
        params += "&NT=" + encodeBooleanParam(nt);
        params += "&QLD=" + encodeBooleanParam(qld);
        params += "&SA=" + encodeBooleanParam(sa);
        params += "&TAS=" + encodeBooleanParam(tas);
        params += "&VIC=" + encodeBooleanParam(vic);
        params += "&WA=" + encodeBooleanParam(wa);

        params += "&postcode=" + URLEncoder.encode(postcode, UTF_8);

//        params += "&searchWidth=" + width;

        params += "&minimumScore=" + minScore;

        results = doRequest("ABRSearchByNameAdvancedSimpleProtocol", params);

        return results;
    }

    private JSONObject doRequest(String service, String parameters) throws IOException,
            FactoryConfigurationError {
        JSONObject result = null;
        String res;
        URL url = new URL("https://abr.business.gov.au/abrxmlsearch/ABRXMLSearch.asmx/" + service + "?authenticationGuid=" + URLEncoder.encode(guid, UTF_8) + parameters);
        Log.i("TAG", "url  " + url);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "text/xml; charset-utf-8");
        connection.connect();
        Log.i("TAG", "data " + connection.getResponseCode());
        int resCode = connection.getResponseCode();
        if (resCode == HttpURLConnection.HTTP_OK) {
            res = readStream(connection.getInputStream());
            XmlToJson xmlToJson = new XmlToJson.Builder(res).build();
            result = xmlToJson.toJson();
        } else if (resCode == 500) {
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Internal server error", Toast.LENGTH_SHORT).show();
                }
            });
        }
//        Log.i("TAG", "data " +res);
//            result = new AbnSearchResult(XMLUtils.DOMParseXML(connection.getInputStream()).getDocumentElement());
        connection.disconnect();
        return result;
    }

    private static String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }

    private static String encodeBooleanParam(boolean value) {
        if (value)
            return "Y";
        else
            return "N";
    }

}
