package com.byteshaft.abnlookup;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    private TextView abnNumber;
    private TextView abnStatus;
    private TextView abnStatusDate;
    private LinearLayout entityLayout;
    private TextView entityTypeValue;
    private TextView entityTypeTitle;
    private TextView acn;
    private TextView acnTitle;
    private String abn;
    private boolean abnStatusValue;
    private String abnDate;
    private String entityType;
    private ArrayList<Serializer> arrayList;
    private String acnNumber;
    private int mode;
    private TextView abnStatusTitle;
    private LinearLayout abnStatusLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        abn = getIntent().getStringExtra("abn");
        abnStatusValue = getIntent().getBooleanExtra("abn_status", false);
        abnDate = getIntent().getStringExtra("abn_from");
        entityType = getIntent().getStringExtra("entity_type");
        mode = getIntent().getIntExtra("mode", 0);
        arrayList = (ArrayList<Serializer>) getIntent().getSerializableExtra("entities");
        acnNumber = getIntent().getStringExtra("acn");
        setContentView(R.layout.details_layout);
        abnNumber = findViewById(R.id.abn_number);
        acnTitle = findViewById(R.id.acn_title);
        acn = findViewById(R.id.acn_number);
        abnStatus = findViewById(R.id.abn_status);
        abnStatusDate = findViewById(R.id.from_date);
        entityLayout = findViewById(R.id.entity_layout);
        entityTypeValue = findViewById(R.id.entity_type_value);
        entityTypeTitle = findViewById(R.id.entity_type_title);
        abnStatusTitle = findViewById(R.id.abn_status_title);
        abnStatusLayout = findViewById(R.id.abn_status_layout);
        if (acnNumber.equals("empty")) {
            acnTitle.setVisibility(View.GONE);
            acn.setVisibility(View.GONE);
        }
        acn.setText(String.valueOf(acnNumber));
        abnNumber.setText(String.valueOf(abn));
        if (abnStatusValue) {
            abnStatus.setText("Active");
            abnStatus.setTextColor(getResources().getColor(android.R.color.holo_green_light));
        } else {
            abnStatusLayout.setVisibility(View.GONE);
            abnStatusTitle.setVisibility(View.GONE);
            abnStatus.setText("Cancelled");
            abnStatus.setTextColor(getResources().getColor(android.R.color.holo_red_light));
        }
        abnStatusDate.setText(abnDate);
        entityTypeValue.setText(entityType);
        if (entityType == null || entityType.trim().isEmpty()) {
            entityTypeValue.setVisibility(View.GONE);
            entityTypeTitle.setVisibility(View.GONE);
        }
        for (Serializer serializer: arrayList) {
            View view = getLayoutInflater().inflate(R.layout.single_entity, null);
            TextView entityName = view.findViewById(R.id.entity_name);
            TextView dates = view.findViewById(R.id.dates);
            LinearLayout abnLayout = view.findViewById(R.id.abn_detail_layout);
            TextView abnNumber = view.findViewById(R.id.text_view_identifier_value);
            TextView abnStatus = view.findViewById(R.id.text_view_state);
            if (serializer.getEntityStatus() != null) {
                abnLayout.setVisibility(View.GONE);
            } else {
                abnLayout.setVisibility(View.VISIBLE);
                if (serializer.isAbnActive()) {
                    abnNumber.setText(serializer.identifierValue);
                    abnStatus.setText("Active");
                    abnStatus.setTextColor(getResources().getColor(android.R.color.holo_green_light));
                } else {
                    abnStatus.setText("Cancelled");
                    abnStatus.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                }
            }
            entityName.setText(serializer.organisationName);
            String datesDetails = null;
            if (serializer.getEffectiveFrom() != null) {
                datesDetails = serializer.getEffectiveFrom() + " / ";
            }
            if (serializer.getEffectiveTo() != null) {
                datesDetails = datesDetails + serializer.getEffectiveTo();
            }
            dates.setText(datesDetails);
            entityLayout.addView(view);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
                default: return false;
        }
    }
}
