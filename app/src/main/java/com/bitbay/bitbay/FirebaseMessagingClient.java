package com.bitbay.bitbay;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;


public class FirebaseMessagingClient {

    public static final String TAG = "DummyTag";
    public static final String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";
    public static final okhttp3.MediaType JSON = okhttp3.MediaType.parse("application/json; charset=utf-8");
    public static final String SERVER_LEGACY_KEY = "AIzaSyD8mX0WRhMdQlI7weDeZrxBVe4p5c9bJz4";

    private OkHttpClient mClient = new OkHttpClient();


    public FirebaseMessagingClient(){
        FirebaseInstanceId.getInstance().getToken();
        Log.i(TAG, "InstanceID token: " + FirebaseInstanceId.getInstance().getToken());
    }

    @SuppressLint("StaticFieldLeak")
    public void sendMessage(final String token, final String title, final String body, final String message) {

        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... params) {
                try {

                    JSONArray regArray = new JSONArray();
                    regArray.put(token);

                    JSONObject root = new JSONObject();
                    JSONObject notification = new JSONObject();
                    notification.put("body", body);
                    notification.put("title", title);

                    JSONObject data = new JSONObject();
                    data.put("message", message);
                    root.put("notification", notification);
                    root.put("data", data);
                    root.put("registration_ids", regArray);

                    String result = postToFCM(root.toString());
                    return result;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                try {
                    JSONObject resultJson = new JSONObject(result);
                    int success, failure;
                    success = resultJson.getInt("success");
                    failure = resultJson.getInt("failure");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    private String postToFCM(String bodyString) throws IOException {

        RequestBody body = RequestBody.create(JSON, bodyString);
        okhttp3.Request request = new okhttp3.Request.Builder()
                .header("Authorization", "key=" + SERVER_LEGACY_KEY)
                .url(FCM_MESSAGE_URL)
                .post(body)
                .build();
        Response response = mClient.newCall(request).execute();
        return response.body().string();
    }
}
