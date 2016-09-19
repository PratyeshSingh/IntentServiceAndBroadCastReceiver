package com.example.pratyeshsingh.wynkworkshop;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.Context;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.pratyeshsingh.wynkworkshop.api.MyDownloader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MyIntentService extends Service {
    private IBinder mBinder = new MyBinder();

    public class MyBinder extends Binder {
        MyIntentService getService() {
            return MyIntentService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        onHandle(intent);
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        onHandle(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        mChronometer.stop();
    }

    ArrayList<String> listData = new ArrayList<>();


    protected void onHandle(Intent intent) {
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
