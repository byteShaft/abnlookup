package com.byteshaft.abnlookup;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class NameDetail extends AppCompatActivity {

    private TextView entityName;
    private TextView abnStatus;
    private TextView entityTpye;
    private TextView gst;
    private TextView mainBusinessLocation;
    private TextView tradingName;
    private TextView abnLastUpdated;
    private NameDetailSerializer nameDetail;
    private LinearLayout businessNamesMainLayout;
    private ArrayList<BusinessName> businessNameArrayList;
    private TextView businessnames;
    private TextView gstTitle;
    private TextView tradingNameTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.name_details_activity);
        nameDetail = (NameDetailSerializer) getIntent().getSerializableExtra("data");
        businessNameArrayList = nameDetail.getBusinessNames();
        entityName = findViewById(R.id.entity_name_value);
        abnStatus = findViewById(R.id.abn_status_value);
        entityTpye = findViewById(R.id.entity_type_value);
        businessnames = findViewById(R.id.business_names);
        tradingNameTitle = findViewById(R.id.trading_name);
        gstTitle = findViewById(R.id.gst);
        gst = findViewById(R.id.gst_value);
        mainBusinessLocation = findViewById(R.id.main_business_location_value);
        tradingName = findViewById(R.id.trading_name_location);
        abnLastUpdated = findViewById(R.id.abn_last_updated_value);
        businessNamesMainLayout = findViewById(R.id.business_names_layout);
        entityName.setText(nameDetail.getEntityName());
        abnStatus.setText(nameDetail.getAbnStatus());
        entityTpye.setText(nameDetail.getEntityType());
        if (nameDetail.getGst() == null || nameDetail.getGst().isEmpty()) {
            gst.setVisibility(View.GONE);
            gstTitle.setVisibility(View.GONE);
        }
        if (nameDetail.getTradingName() == null || nameDetail.getTradingName().isEmpty()) {
            tradingName.setVisibility(View.GONE);
            tradingNameTitle.setVisibility(View.GONE);
        }
        gst.setText(nameDetail.getGst());
        mainBusinessLocation.setText(nameDetail.getBusinessLocation());
        tradingName.setText(nameDetail.getTradingName());
        abnLastUpdated.setText(nameDetail.getLastUpdated());

        if (businessNameArrayList != null) {
            for (BusinessName businessName : businessNameArrayList) {
                View view = getLayoutInflater().inflate(R.layout.single_entity, null);
                TextView entityName = view.findViewById(R.id.entity_name);
                TextView dates = view.findViewById(R.id.dates);
                LinearLayout main = view.findViewById(R.id.abn_detail_layout);
                main.setVisibility(View.GONE);
                entityName.setText(businessName.getName());
                dates.setText("From " + businessName.getFrom());
                businessNamesMainLayout.addView(view);
            }
        } else {
            if (businessNameArrayList == null || businessNameArrayList.size() == 0) {
                businessnames.setVisibility(View.GONE);
                businessNamesMainLayout.setVisibility(View.GONE);
            }
        }
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }
}
