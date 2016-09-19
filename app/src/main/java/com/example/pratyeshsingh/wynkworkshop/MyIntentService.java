package com.example.pratyeshsingh.wynkworkshop;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import com.example.pratyeshsingh.wynkworkshop.api.MyDownloader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MyIntentService extends IntentService {

    public MyIntentService() {
        super("MyIntentService");
    }

    ArrayList<String> listData = new ArrayList<>();

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String imageUrl = intent.getStringExtra("imageUrl");
            listData.add(imageUrl);


            ExecutorService executor = Executors.newFixedThreadPool(2);
            Iterator<String> it = listData.iterator();
            while (it.hasNext()) {
                String _imageUrl = it.next();
                Runnable worker = new WorkerThread(_imageUrl);
                executor.execute(worker);
            }
            executor.shutdown();
            while (!executor.isTerminated()) {
            }

            String filename = imageUrl.substring(imageUrl.lastIndexOf('/') + 1, imageUrl.length());
            MyDownloader.downloadFile(imageUrl, filename);

        }
    }

    class WorkerThread implements Runnable {
        String imageUrl;

        public WorkerThread(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        @Override
        public void run() {
            String filename = imageUrl.substring(imageUrl.lastIndexOf('/') + 1, imageUrl.length());
            MyDownloader.downloadFile(imageUrl, filename);

            listData.remove(imageUrl);
        }
    }

}
