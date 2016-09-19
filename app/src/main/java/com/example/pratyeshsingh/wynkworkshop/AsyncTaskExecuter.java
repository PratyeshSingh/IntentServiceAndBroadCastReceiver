package com.example.pratyeshsingh.wynkworkshop;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;


import com.example.pratyeshsingh.wynkworkshop.api.HTTP_ApiRequest;

import java.util.Hashtable;

public class AsyncTaskExecuter extends AsyncTask<Object, Object, Object[]> {

    private ProgressDialog dialog;
    private Context context;
    private AsyncTaskExecuterListener asyncTaskExecuterListener;
    private String searchUrl;
    private String request_body;
    private String requestType;
    private Hashtable<String, String> header;
    boolean isDialogueShow;

    public interface AsyncTaskExecuterListener {
        public void notifyRespons(Object[] result);
    }

    public AsyncTaskExecuter(Context context, AsyncTaskExecuterListener asyncTaskExecuterListener, String searchUrl, String request_body, String requestType, Hashtable<String, String> header,
                             boolean isDialogueShow) {

        this(context, asyncTaskExecuterListener, searchUrl, request_body, requestType, header);
        this.isDialogueShow = isDialogueShow;
    }

    public AsyncTaskExecuter(Context context, AsyncTaskExecuterListener asyncTaskExecuterListener, String searchUrl, String request_body, String requestType, Hashtable<String, String> header) {
        this.context = context;
        this.asyncTaskExecuterListener = asyncTaskExecuterListener;
        this.searchUrl = searchUrl;
        this.request_body = request_body;
        this.requestType = requestType;
        this.header = header;
        dialog = new ProgressDialog(context);
    }

    @Override
    protected void onPreExecute() {

        super.onPreExecute();
        if (isDialogueShow) {
            dialog.setMessage("Loading...");
            dialog.show();
        }

    }

    @Override
    protected void onProgressUpdate(Object... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected Object[] doInBackground(Object... params) {

        return HTTP_ApiRequest.getApiResult(searchUrl, request_body, requestType, header);
    }

    @Override
    protected void onPostExecute(Object[] result) {

        super.onPostExecute(result);
        if (dialog.isShowing())
            dialog.dismiss();
        asyncTaskExecuterListener.notifyRespons(result);
    }
}
