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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import static android.provider.ContactsContract.Intents.Insert.NAME;
import static com.byteshaft.abnlookup.AppGlobals.ABN;
import static com.byteshaft.abnlookup.AppGlobals.ACN;
import static com.byteshaft.abnlookup.AppGlobals.MODE;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editText;
    private TextView postCode;
    private Button query;
    private MultiSelectionSpinner spinner;
    private AdView adView;
    public static HashMap<String, Boolean> selectedStates;
    private Spinner nameType;
    private String nameTypeValue;
    public static HashMap<String, Boolean> selectedNameType;
    public static String postCodeValue = "All";
    private TextView state;
    private TextView postCodeTitle;
    private TextView nameTypeTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        selectedStates = new HashMap<>();
        selectedNameType = new HashMap<>();
        MobileAds.initialize(this, getString(R.string.test_id));
        postCode = findViewById(R.id.text_view_postcode);
        postCode.setTypeface(AppGlobals.robotoRegular);
        state = findViewById(R.id.tv_select_state);
        nameTypeTitle = findViewById(R.id.name_type);
        nameTypeTitle.setTypeface(AppGlobals.bold);
        postCodeTitle = findViewById(R.id.post_code);
        postCodeTitle.setTypeface(AppGlobals.bold);
        state.setTypeface(AppGlobals.bold);
        postCode.setText(postCodeValue);
        editText = findViewById(R.id.editText);
        editText.setTypeface(AppGlobals.robotoRegular);

        spinner = findViewById(R.id.spinner);
        query = findViewById(R.id.button);
        query.setTypeface(AppGlobals.robotoItalic);
        adView = findViewById(R.id.adView);
        nameType = findViewById(R.id.select_name);
        nameType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                nameTypeValue = (String) parent.getItemAtPosition(position);
                setNamesValues(nameTypeValue);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(getResources().getString(R.string.test_id));
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        adView.loadAd(adRequest);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                Log.i("TAG", "onAdFailedToLoad" + i);
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                Log.i("TAG", "onAdLoaded");
            }
        });

        String[] array = getResources().getStringArray(R.array.name_array);
        for (String name: array) {
            selectedNameType.put(name, false);

        }

        List<String> list = new ArrayList<>();
        list.add("ACT");
        list.add("NSW");
        list.add("NT");
        list.add("QLD");
        list.add("SA");
        list.add("TAS");
        list.add("VIC");
        list.add("WA");

        selectedStates.put("ACT", false);
        selectedStates.put("NSW", false);
        selectedStates.put("NT", false);
        selectedStates.put("QLD", false);
        selectedStates.put("SA", false);
        selectedStates.put("TAS", false);
        selectedStates.put("VIC", false);
        selectedStates.put("WA", false);
        spinner.setItems(list);
        spinner.setSelection(list);
        spinner.getSelectedItemsAsString();
        Log.wtf("Items ", spinner.getSelectedItemsAsString());
        query.setOnClickListener(this);
        postCode.setOnClickListener(this);
    }

    private void setNamesValues(String selected) {
        for (Map.Entry<String,Boolean> entry : selectedNameType.entrySet()) {
            String key = entry.getKey();
            Boolean value = entry.getValue();
            if (key.contentEquals(selected)) {
                selectedNameType.put(key, true);
            } else {
                selectedNameType.put(key, false);
            }
            // do stuff
        }
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
                    postCodeValue = wishlist;
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
                Intent intent = new Intent(getApplicationContext(), LoadingActivity.class);
                if (query.matches("[0-9]+") && query.length() > 2) {
                    if (query.length() == 9) {
                        MODE = ACN;
                    } else {
                        MODE = ABN;
                    }
                } else if (isAlpha(query)) {
                    MODE = AppGlobals.NAME;
                }
                intent.putExtra("post_code", postCode.getText().toString());
                intent.putExtra("query", query);
                intent.putExtra("mode", MODE);
                intent.putExtra("name_type", nameTypeValue);
                startActivity(intent);
                break;
        }
    }

    public boolean isAlpha(String name) {
        return name.matches("[a-zA-Z]+");
    }
}
