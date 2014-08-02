package com.metyou.fragments;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.metyou.R;
import com.metyou.social.User;

import java.util.List;

/**
 * Created by mihai on 7/25/14.
 */
public class UserListAdapter extends ArrayAdapter<User> {

    List<User> users;
    public UserListAdapter(Context context, int resource, List<User> objects) {
        super(context, resource, objects);
        this.users = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.user_row, parent, false);
        ImageView photo = (ImageView) rowView.findViewById(R.id.user_photo);
        TextView name = (TextView) rowView.findViewById(R.id.user_name);
        name.setText(users.get(position).getName());
        photo.setImageBitmap(users.get(position).getPhoto());
        return rowView;
    }
}
