package com.byteshaft.abnlookup;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private EditText editText;
    private Button query;
    private AbnSearchWSHttpGet abnSearchWSHttpGet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.editText);
        query = findViewById(R.id.button);
        abnSearchWSHttpGet = new AbnSearchWSHttpGet();
        query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = editText.getText().toString();
                new Query().execute(query);
            }
        });
    }

    private class Query extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            abnSearchWSHttpGet.doQuery(strings[0]);
            return null;
        }
    }
}
