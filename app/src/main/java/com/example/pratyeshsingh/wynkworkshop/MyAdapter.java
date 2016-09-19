package com.example.pratyeshsingh.wynkworkshop;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by pratyeshsingh on 19/09/16.
 */
public class MyAdapter<T> extends BaseAdapter {
    private LayoutInflater inflater = null;
    ArrayList<T> dataList;
    Activity activity;
    ServiceConnection serviceConnection;
    HashMap<String, String> imageList;

    public MyAdapter(HashMap<String, String> imageList, Activity activity, ArrayList dataList, ServiceConnection serviceConnection) {
        this.imageList = imageList;
        this.activity = activity;
        this.serviceConnection = serviceConnection;
        this.dataList = dataList;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {

        return dataList.size();
    }

    @Override
    public T getItem(int position) {

        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    public void refresh() {
        notifyDataSetChanged();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder = null;
        if (convertView == null) {
            vi = inflater.inflate(R.layout.row_item, null);
            holder = new ViewHolder(vi);

            vi.setTag(holder);
        } else
            holder = (ViewHolder) vi.getTag();

        T item = getItem(position);
        if (item instanceof MyContent) {
            holder.imageName.setText(((MyContent) item).getImageDescription());
            holder.download.setTag(((MyContent) item).getImageUrl());
            if (((MyContent) item).isStatus())
                holder.download.setText("Downloaded");
        }

        return vi;
    }

    class ViewHolder implements View.OnClickListener {
        TextView download;
        TextView imageName;

        public ViewHolder(View vi) {
            imageName = (TextView) vi.findViewById(R.id.imageName);
            download = (TextView) vi.findViewById(R.id.download);

            download.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            String imageUrl = (String) v.getTag();
            onDownloadClick(imageUrl);
        }


        public void onDownloadClick(String imageUrl) {

            String filename = imageUrl.substring(imageUrl.lastIndexOf('/') + 1, imageUrl.length());
            if (!imageList.containsKey(filename)) {
                imageList.put(filename, imageUrl);
                Intent intent = new Intent(activity, MyIntentService.class);
                intent.putExtra("imageUrl", imageUrl);
                activity.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
            }
        }


    }
}
