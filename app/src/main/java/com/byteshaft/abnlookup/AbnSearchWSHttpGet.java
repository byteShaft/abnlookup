package com.byteshaft.abnlookup;

import android.util.Log;

import com.byteshaft.abnlookup.XMLUtils;

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
import javax.xml.soap.SOAPBody;

import org.json.JSONObject;
import org.xml.sax.SAXException;

import fr.arnaudguyon.xmltojsonlib.XmlToJson;

public class AbnSearchWSHttpGet {

    private static final String UTF_8 = "UTF-8";

    public void doQuery(String query) {
        try {
            String guid = "a1013045-797c-45d5-b573-8ee5526c69ec";
            String abn = query;

            JSONObject result = searchByABNv200506(guid, abn, true);
            Log.i("TAG", " result  "+ result);
        } catch (Exception e) {
            System.err.println("Caught exception : " + e);
            e.printStackTrace(System.err);
        }
    }

    public static JSONObject searchByABN(String guid, String abn, boolean includeHistorical) throws URISyntaxException, IOException,
            SAXException, ParserConfigurationException, FactoryConfigurationError {
        JSONObject results = null;

        String params = "";

        params += "&includeHistoricalDetails=" + encodeBooleanParam(includeHistorical);
        params += "&searchString=" + URLEncoder.encode(abn, UTF_8);

        results = doRequest(guid, "ABRSearchByABN", params);

        return results;
    }

    public static JSONObject searchByACN(String guid, String acn, boolean includeHistorical) throws URISyntaxException, IOException,
            SAXException, ParserConfigurationException, FactoryConfigurationError {
        JSONObject results = null;

        String params = "";

        params += "&includeHistoricalDetails=" + encodeBooleanParam(includeHistorical);
        params += "&searchString=" + URLEncoder.encode(acn, UTF_8);

        results = doRequest(guid, "ABRSearchByASIC", params);

        return results;
    }

    public static JSONObject searchByABNv200506(String guid, String abn, boolean includeHistorical) throws URISyntaxException, IOException,
            SAXException, ParserConfigurationException, FactoryConfigurationError {
        JSONObject results = null;

        String params = "";

        params += "&includeHistoricalDetails=" + encodeBooleanParam(includeHistorical);
        params += "&searchString=" + URLEncoder.encode(abn, UTF_8);

        results = doRequest(guid, "SearchByABNv201408", params);

        return results;
    }

    public static JSONObject searchByACNv200506(String guid, String acn, boolean includeHistorical) throws URISyntaxException, IOException,
            SAXException, ParserConfigurationException, FactoryConfigurationError {
        JSONObject results = null;

        String params = "";

        params += "&includeHistoricalDetails=" + encodeBooleanParam(includeHistorical);
        params += "&searchString=" + URLEncoder.encode(acn, UTF_8);

        results = doRequest(guid, "SearchByASICv200506", params);

        return results;
    }

    public JSONObject searchByNameSimpleProtocol(String guid, String name, boolean legal, boolean trading, boolean act, boolean nsw,
                                                      boolean nt, boolean qld, boolean sa, boolean tas, boolean vic, boolean wa, String postcode) throws URISyntaxException, IOException,
            SAXException, ParserConfigurationException, FactoryConfigurationError {
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

        results = doRequest(guid, "ABRSearchByNameSimpleProtocol", params);

        return results;
    }

    public static JSONObject searchByNameAdvancedSimpleProtocol(String guid, String name, boolean legal, boolean trading, boolean act,
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

        results = doRequest(guid, "ABRSearchByNameAdvancedSimpleProtocol", params);

        return results;
    }

    private static JSONObject doRequest(String guid, String service, String parameters) throws IOException,
             FactoryConfigurationError {
        JSONObject result = null;
        String res = null;

        URL url = new URL("http://abr.business.gov.au/abrxmlsearch/ABRXMLSearch.asmx/" + service + "?authenticationGuid=" + URLEncoder.encode(guid, UTF_8) + parameters);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "text/xml; charset-utf-8");
        connection.connect();
        Log.i("TAG", "data " + connection.getResponseCode());
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            res = readStream(connection.getInputStream());
            XmlToJson xmlToJson = new XmlToJson.Builder(res).build();
            result = xmlToJson.toJson();
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
