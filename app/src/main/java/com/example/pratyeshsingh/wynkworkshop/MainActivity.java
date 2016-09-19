package com.example.pratyeshsingh.wynkworkshop;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.pratyeshsingh.wynkworkshop.api.MyDownloader;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

public class MainActivity extends AppCompatActivity {

    int count = 0;
    private String URL = "http://52e4a06a.ngrok.io/fetch.php?offset=";//  2
    ArrayList<MyContent> listData = new ArrayList<>();
    MyAdapter<MyContent> mMyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = (ListView) findViewById(R.id.listView);
        mMyAdapter = new MyAdapter<>(this, listData);
        listView.setAdapter(mMyAdapter);

        loadList();

    }

    private void loadList() {
        final Hashtable<String, String> header = new Hashtable<>();
        final String url = URL + count;

        AsyncTaskExecuter.AsyncTaskExecuterListener asyncTaskExecuterListener = new AsyncTaskExecuter.AsyncTaskExecuterListener() {

            @Override
            public void notifyRespons(Object[] result) {
                listData.clear();

                if (result != null && (Integer) result[0] == 200) {

                    JSONObject da = (JSONObject) result[1];
                    try {
                        JsonParser.parseData(da.getJSONArray("images"), listData);
                        mMyAdapter.refresh();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.internal_error), Toast.LENGTH_LONG).show();

            }
        };

        if (checkInternetConnection()) {
            new AsyncTaskExecuter(this, asyncTaskExecuterListener, url, "", "GET", header, true).execute();
        } else {
            Toast.makeText(this, getResources().getString(R.string.no_internet_connection_found), Toast.LENGTH_LONG).show();
        }
    }

    public boolean checkInternetConnection() {

        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        // ARE WE CONNECTED TO THE NET
        if (conMgr.getActiveNetworkInfo() != null

                && conMgr.getActiveNetworkInfo().isAvailable()

                && conMgr.getActiveNetworkInfo().isConnected()) {

            return true;

        } else {
            return false;
        }
    }

    public void previouse(View view) {
        if (count > 0) {
            count--;
            loadList();
        }
    }

    public void nextView(View view) {
        count++;
        loadList();
    }
}
