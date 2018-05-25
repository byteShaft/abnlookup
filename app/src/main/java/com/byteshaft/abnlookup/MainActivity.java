package com.byteshaft.abnlookup;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.claudiodegio.msv.MaterialSearchView;
import com.claudiodegio.msv.OnSearchViewListener;


public class MainActivity extends AppCompatActivity {

    private EditText editText;
    private Button query;
    private AbnSearchWSHttpGet abnSearchWSHttpGet;
    private Toolbar toolbar;
    private MaterialSearchView materialSearchView;

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
        query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = editText.getText().toString();
                new Query().execute(query);
            }
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


    private class Query extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            abnSearchWSHttpGet.doQuery(strings[0]);
            return null;
        }
    }
}
