package com.example.pratyeshsingh.wynkworkshop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by pratyeshsingh on 19/09/16.
 */
public class JsonParser {

    public static void parseData(JSONArray res, ArrayList<MyContent> listData) {
        try {
            for (int i = 0; i < res.length(); i++) {

                JSONObject data = res.getJSONObject(i);

                MyContent mMyContent = new MyContent();

                String imageUrl = "";
                if (data.has("imageUrl"))
                    imageUrl = data.getString("imageUrl");

                mMyContent.setImageUrl(imageUrl);

                String imageDescription = "";
                if (data.has("imageDescription"))
                    imageDescription = data.getString("imageDescription");

                mMyContent.setImageDescription(imageDescription);


                listData.add(mMyContent);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
