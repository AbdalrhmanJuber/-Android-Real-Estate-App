package com.example.a1211769_courseproject;

import android.app.Activity;
import android.os.AsyncTask;

import java.util.List;

public class ConnectionAsyncTask extends AsyncTask<String, String, String> {
    Activity activity;

    public ConnectionAsyncTask(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected void onPreExecute(){
        ((MainActivity) activity).setButtonText("connecting");
        super.onPreExecute();
        ((MainActivity) activity).setProgress(true);
    }

    @Override
    protected String doInBackground(String... params) {
        String result = HttpManager.getData(params[0]);
        return result;
    }

    @Override
    protected void onPostExecute(String result){
        super.onPostExecute(result);
        ((MainActivity) activity).setButtonText("connected");
        ((MainActivity) activity).setProgress(false);
        List<Property> properties = JsonParser.parseProperties(result);
        ((MainActivity) activity).fillStudents(students);
    }
}