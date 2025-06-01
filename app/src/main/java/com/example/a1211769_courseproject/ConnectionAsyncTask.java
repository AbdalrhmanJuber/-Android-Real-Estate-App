package com.example.a1211769_courseproject;

import android.app.Activity;
import android.os.AsyncTask;

public class ConnectionAsyncTask extends AsyncTask<String, String, String> {
    protected Activity activity;

    public ConnectionAsyncTask(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // This is now handled in subclasses
    }

    @Override
    protected String doInBackground(String... params) {
        return HttpManager.getData(params[0]);
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        // This is now handled in subclasses
    }
}

