package com.example.pratyeshsingh.wynkworkshop;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.pratyeshsingh.wynkworkshop.api.MyDownloader;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

public class MainActivity extends AppCompatActivity {

    final public static String action1 = "com.example.MyBroadCastReceiver_START_DOWNLOAD_SERVICE";
    final public static String action3 = "com.example.MyBroadCastReceiver_START_DOWNLOAD_ALL_SERVICE";
    final public static String action2 = "com.example.MyBroadCastReceiver_STOP_SERVICE";
    int count = 0;
    private String URL = "http://52e4a06a.ngrok.io/fetch.php?offset=";//  2

    ArrayList<MyContent> listData = new ArrayList<>();
    HashMap<String, String> imageList = new HashMap<>();

    MyAdapter<MyContent> mMyAdapter;
    ProgressBar progressBar;
    Intent serviceIntent;


    private MyBroadCastReceiver mMyBroadCastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        ListView listView = (ListView) findViewById(R.id.listView);
        mMyAdapter = new MyAdapter<>(this, listData);
        listView.setAdapter(mMyAdapter);
        loadList();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // The filter's action is BROADCAST_ACTION
        IntentFilter statusIntentFilter = new IntentFilter();
        statusIntentFilter.addAction(action1);
        statusIntentFilter.addAction(action2);
        statusIntentFilter.addAction(action3);

        // Instantiates a new DownloadStateReceiver
        mMyBroadCastReceiver = new MyBroadCastReceiver();

        // Registers the MyBroadCastReceiver and its intent filters
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mMyBroadCastReceiver, statusIntentFilter);
//        registerReceiver(mMyBroadCastReceiver, statusIntentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        unregisterReceiver(mMyBroadCastReceiver);
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mMyBroadCastReceiver);
    }

    public void downloadAll(View view) {
        Intent intent = new Intent(MainActivity.action3);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
//        sendBroadcast(intent);
    }

    public class MyBroadCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {


            switch (intent.getAction()) {
                case action1:
                    showDialogue();
                    ArrayList<String> _listData = new ArrayList<>();
                    String imageUrl = intent.getStringExtra("imageUrl");
                    String filename = imageUrl.substring(imageUrl.lastIndexOf('/') + 1, imageUrl.length());

                    if (!imageList.containsKey(filename)) {
                        imageList.put(filename, imageUrl);
                        _listData.add(imageUrl);
                    }

                    serviceIntent = new Intent(context, MyIntentService.class);
//                        serviceIntent.putExtra("imageUrl", imageUrl);
                    serviceIntent.putStringArrayListExtra("listData", _listData);
                    startService(serviceIntent);

                    break;
                case action3:

                    showDialogue();
                    _listData = new ArrayList<>();

                    for (MyContent myContent : listData) {

                        imageUrl = myContent.getImageUrl();
                        filename = imageUrl.substring(imageUrl.lastIndexOf('/') + 1, imageUrl.length());

                        if (!imageList.containsKey(filename)) {
                            imageList.put(filename, imageUrl);
                            _listData.add(imageUrl);
                        }
                    }

                    serviceIntent = new Intent(context, MyIntentService.class);
                    serviceIntent.putStringArrayListExtra("listData", _listData);
                    startService(serviceIntent);

                    break;
                case action2:
                    hideDialogue();
                    break;
            }


        }
    }

    private void showDialogue() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideDialogue() {
        progressBar.setVisibility(View.GONE);
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
