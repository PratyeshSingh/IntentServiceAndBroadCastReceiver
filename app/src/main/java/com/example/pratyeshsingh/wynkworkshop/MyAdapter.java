package com.example.pratyeshsingh.wynkworkshop;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
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

    public MyAdapter(Activity activity, ArrayList dataList) {
        this.activity = activity;
        this.dataList = dataList;
        this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
            Intent intent = new Intent(MainActivity.action1);
            intent.putExtra("imageUrl", imageUrl);
            LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);
//            activity.sendBroadcast(intent);
        }
    }
}
