package com.metyou.fragments.userlist;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;
import com.metyou.R;
import com.metyou.cloud.services.model.UserEncountered;
import com.metyou.social.User;
import com.metyou.util.ImageCache;
import com.metyou.util.ImageFetcher;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
/**
 * Created by mihai on 7/25/14.
 */
public class UserListAdapter extends ArrayAdapter<ListRow> {

    private static final String TAG = "USER_LIST_ADAPTER";
    private static final String IMAGE_CACHE_DIR = "thumbs";
    List<ListRow> users;
    private ImageFetcher imageFetcher;
    public UserListAdapter(Context context, int resource, List<ListRow> objects, ImageFetcher imageFetcher) {
        super(context, resource, objects);
        this.users = objects;
        this.imageFetcher = imageFetcher;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView;
        if (users.get(position) instanceof UserRow) {
            UserRow userRow = (UserRow) users.get(position);
            rowView = inflater.inflate(R.layout.user_row, parent, false);
            ImageView photo = (ImageView) rowView.findViewById(R.id.user_photo);
            TextView name = (TextView) rowView.findViewById(R.id.user_name);
            name.setText(userRow.getFirstName());
            imageFetcher.loadProfileFBImage(userRow.getSocialId(), photo);
            Log.d(TAG, userRow.getSocialId());
        } else {

//            if (convertView != null) {
//                rowView = convertView;
//            } else {
                rowView = inflater.inflate(R.layout.loading_layout, parent, false);
//            }
        }
        return rowView;
    }
}
