package com.example.pratyeshsingh.wynkworkshop.api;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * @author Pratyesh Singh
 */
public class MyDownloader implements Runnable {

    HttpListener httpListener;
    String resMessage = "";
    int resCode = -11;
    String urlstring;
    String requeststring = null;
    String requestType = "";
    Hashtable<String, String> header;
    int reqID;
    String filename;
    String stateHTTP = "";
    String uuid = "";
    Context context = null;

    /**
     * Constructor : MPDownloader
     * <p>
     * For POST request
     *
     * @param urlstring
     * @param requeststring
     */
    public MyDownloader(String urlstring, String request, String filename, int reqID, String requestType, Hashtable<String, String> header) {
        this.urlstring = urlstring;
        this.requeststring = request;
        this.reqID = reqID;
        this.filename = filename;
        this.requestType = requestType;
        this.header = header;
    }

    /**
     * For GET request
     *
     * @param urlstring
     */
    public MyDownloader(String urlstring) {
        this.urlstring = urlstring;
        header = new Hashtable<String, String>();
    }

    public void setContext(Context context) {
        this.context = context;
    }

    /**
     * Return requested url
     *
     * @return
     */

    public String getURL() {
        return urlstring;
    }

    /**
     * @return filename
     */
    public String getFilename() {
        return filename;
    }

    /**
     * @return request id
     */
    public int getReqID() {
        return reqID;
    }

    /**
     * Return Response String
     *
     * @return
     */
    public String getResponse() {
        return resMessage;
    }

    /**
     * Return Response Code
     *
     * @return
     */
    public int getResCode() {
        return resCode;
    }

    /**
     * Add header to SOAP Envelope.
     *
     * @param key
     * @param value
     */
    public void addHeader(String key, String value) {
        this.header.put(key, value);
    }

    /**
     * @param httpListener
     */

    public void addHttpLIstener(HttpListener httpListener) {
        this.httpListener = httpListener;
    }

    /**
     * Send the POST request to requested url of non-blocking
     */
    public void sendPost() {
        stateHTTP = "Non-Blocking state: ";
        Log.i(">>>>>>>>>>>>>>", "in the send post method");
        Thread t = new Thread(this);
        t.start();
    }

    /**
     * Send the POST request to requested url of blocking
     */
    public void sendPostBlocking() {
        stateHTTP = "Blocking state: ";
        send();
    }

    public void run() {
        send();
    }

    /**
     * Called to download the XML file from the server.
     */

    public void send() {
        try {

            URL connectURL = new URL(urlstring);

            HttpURLConnection hc = (HttpURLConnection) connectURL.openConnection();

            try {
                if (requeststring != null && !requestType.trim().equalsIgnoreCase("DELETE")) {

                    hc.setDoInput(true);
                    hc.setDoOutput(true);
                    hc.setUseCaches(true);

                    if (requestType.trim().equalsIgnoreCase("PUT")) {
                        hc.setRequestMethod("PUT");
                    } else {
                        hc.setRequestMethod("POST");
                    }

                    // hc.setConnectTimeout(1);
                    hc.setConnectTimeout(5 * 1000);// 5 seconds
                    hc.setReadTimeout(10000 * 100);

                    Enumeration<String> kyes = header.keys();
                    while (kyes.hasMoreElements()) {
                        String key = (String) kyes.nextElement();
                        String value = (String) header.get(key);
                        hc.setRequestProperty(key, value);
                    }
                    hc.setRequestProperty("Connection", "close");
                    hc.setRequestProperty("Content-Length", "" + requeststring.length());
                    // hc.setRequestProperty("Accept-Encoding", "gzip");
                    OutputStream dos = hc.getOutputStream();
                    try {
                        byte[] request_body = requeststring.getBytes();
                        dos.write(request_body);
                        dos.close();
                        // Utils.print("SRDownloader.send() "+stateHTTP+"::Output Stream closed 1 ::");
                    } finally {
                        try {
                            dos.close();
                        } catch (IOException ioe) {
                            ioe.printStackTrace();
                        }
                    }
                } else if (requestType.trim().equalsIgnoreCase("DELETE")) {
                    hc.setRequestMethod("DELETE");
                    hc.setConnectTimeout(5 * 1000);
                    Enumeration<String> kyes = header.keys();
                    while (kyes.hasMoreElements()) {
                        String key = (String) kyes.nextElement();
                        String value = (String) header.get(key);
                        hc.setRequestProperty(key, value);
                    }
                } else {
                    hc.setRequestMethod("GET");
                    hc.setConnectTimeout(5 * 1000);

                    Enumeration<String> kyes = header.keys();
                    while (kyes.hasMoreElements()) {
                        String key = (String) kyes.nextElement();
                        String value = (String) header.get(key);
                        hc.setRequestProperty(key, value);
                    }
                }

                resCode = hc.getResponseCode();
                if (resCode == 200) {
                    InputStream is = hc.getInputStream();
                    try {
                        int length;
                        StringBuffer sb = new StringBuffer();
                        byte[] readChunk = new byte[1024 * 2];
                        while ((length = is.read(readChunk)) > 0) {
                            sb.append(new String(readChunk, 0, length));
                        }
                        resMessage = sb.toString();
                        is.close();
                    } finally {
                        try {
                            is.close();
                        } catch (IOException ioe) {
                            ioe.printStackTrace();
                        }
                    }
                } else if (resCode == 400) {
                    InputStream is = hc.getInputStream();
                    try {
                        int length;
                        StringBuffer sb = new StringBuffer();
                        byte[] readChunk = new byte[1024 * 2];
                        while ((length = is.read(readChunk)) > 0) {
                            sb.append(new String(readChunk, 0, length));
                        }
                        resMessage = sb.toString();
                        is.close();
                    } finally {
                        try {
                            is.close();
                        } catch (IOException ioe) {
                            ioe.printStackTrace();
                        }
                    }
                } else {
                    resMessage = "ERROR";
                }
            } catch (SocketTimeoutException ss) {
                resMessage = "TIMEOUT";
            } catch (Exception e) {
                resMessage = "ERROR";
                e.printStackTrace();
            } finally {
                hc.disconnect();
                httpListener.notifyRespons(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // method to upload a file to the web service
    public void uploadFile(String urlString, String existingFileName) {

        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        // DataInputStream inputStream = null;
        String pathToOurFile = existingFileName;
        String urlServer = urlString;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;

        try {
            FileInputStream fileInputStream = new FileInputStream(new File(pathToOurFile));

            URL url = new URL(urlServer);
            connection = (HttpURLConnection) url.openConnection();

            // Allow Inputs & Outputs
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            // Enable POST method
            connection.setRequestMethod("POST");

            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            outputStream = new DataOutputStream(connection.getOutputStream());
            // outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + pathToOurFile + "\"" + lineEnd);
            // outputStream.writeBytes(lineEnd);

            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // Read file
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                outputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            }
            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // Responses from the server (code and message)
            int serverResponseCode = connection.getResponseCode();
            String serverResponseMessage = connection.getResponseMessage();
            fileInputStream.close();
            outputStream.flush();
            outputStream.close();
        } catch (Exception ex) {
            // Exception handling
            Log.e("error in upload", "" + ex.getMessage());
        }
    }

    public static void downloadFile(String urlstring, String filename) {
        int count;
        try {
            URL url = new URL(urlstring);
            URLConnection conection = url.openConnection();
            conection.connect();
            // getting file length
            int lenghtOfFile = conection.getContentLength();

            // input stream to read file - with 8k buffer
            InputStream input = new BufferedInputStream(url.openStream(), 8192);

            String imagePath = Environment.getExternalStorageDirectory().toString();

            // Output stream to write file
            OutputStream output = new FileOutputStream(imagePath + "/" + filename);

            byte data[] = new byte[1024];

            long total = 0;

            while ((count = input.read(data)) != -1) {
                total += count;
                // publishing the progress....
                // After this onProgressUpdate will be called
//				publishProgress(""+(int)((total*100)/lenghtOfFile));

                // writing data to file
                output.write(data, 0, count);
            }

            // flushing output
            output.flush();

            // closing streams
            output.close();
            input.close();

        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
        }
    }

    /**
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * @param uuid the uuid to set
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
