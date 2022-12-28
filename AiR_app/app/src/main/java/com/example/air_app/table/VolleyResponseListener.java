package com.example.air_app.table;

import org.json.JSONArray;

public interface VolleyResponseListener {
    void onError(String message);
    void onResponse(JSONArray response);
}

