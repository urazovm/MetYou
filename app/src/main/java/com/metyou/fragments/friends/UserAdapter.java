package com.metyou.fragments.friends;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.metyou.R;
import com.metyou.util.ImageFetcher;

import java.util.List;

/**
 * Created by mihai on 7/25/14.
 */
public class UserAdapter extends ArrayAdapter<ListRow> {

    private static final String TAG = "USER_LIST_ADAPTER";
    private static final String IMAGE_CACHE_DIR = "thumbs";
    List<ListRow> users;
    private ImageFetcher imageFetcher;
    public UserAdapter(Context context, int resource, List<ListRow> objects, ImageFetcher imageFetcher) {
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
        } else {
            rowView = inflater.inflate(R.layout.loading_layout, parent, false);
        }
        return rowView;
    }

    public boolean contains(UserRow ur) {
        return users.contains(ur);
    }

    public void update(UserRow ur) {
        int index = users.indexOf(ur);
        users.set(index, ur);
    }
}
