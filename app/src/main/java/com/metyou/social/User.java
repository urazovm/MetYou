package com.metyou.social;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by mihai on 7/25/14.
 */
public class User {

    public interface UpdateListener {
        void updateUserList();
    }

    private UpdateListener updateListener;
    private String id;
    private String name;
    private Bitmap photo;
    private Handler uiHandler;

    public User(String id, UpdateListener listener) {
        this.updateListener = listener;
        this.id = id;
        name="mihai";
        //fetchUserInformation();
        //uiHandler = new Handler(Looper.getMainLooper());
    }

    private void fetchUserInformation() {
        final Session session = Session.getActiveSession();
        if (session != null && session.isOpened()) {

                Request request = new Request(session, "/" + id, null, HttpMethod.GET, new Request.Callback() {

                @Override
                public void onCompleted(Response response) {
                    FacebookRequestError requestError = response.getError();
                    if (requestError != null) {
                        Log.d("Facebook request error", requestError.getErrorMessage());
                    } else {
                        GraphObject graphObject = response.getGraphObject();
                        name = graphObject.getProperty("first_name").toString();
                        updateListener.updateUserList();
                    }
                }
            });
            request.executeAsync();
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        InputStream inputStream = (new URL("http://graph.facebook.com/" + id + "/picture/?redirect=false")).openConnection().getInputStream();
                        BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
                        StringBuilder bitmapURLString = new StringBuilder();
                        String line;
                        while ((line = r.readLine()) != null) {
                            bitmapURLString.append(line);
                        }

                        JSONObject obj = new JSONObject(bitmapURLString.toString());
                        String imageURLString = obj.getJSONObject("data").getString("url");
                        photo = BitmapFactory.decodeStream(new URL(imageURLString).openConnection().getInputStream());
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            updateListener.updateUserList();
                        }
                    });
                }
            });
        }
    }

    public String getUserId() {
        return id;
    }


    public String getName() {
        return name;
    }

    public Bitmap getPhoto() {
        return photo;
    }


}
