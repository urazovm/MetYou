package com.metyou.fragments.friends;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.metyou.R;
import com.metyou.UserPhotos;
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
            final UserRow userRow = (UserRow) users.get(position);
            rowView = inflater.inflate(R.layout.user_row, parent, false);
            ImageView photo = (ImageView) rowView.findViewById(R.id.user_photo);
            photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), UserPhotos.class);
                    intent.putExtra("socialId", userRow.getSocialId());
                    intent.putExtra("firstName", userRow.getFirstName());
                    getContext().startActivity(intent);
                }
            });
            View rightSide = rowView.findViewById(R.id.user_right_side);

            TextView name = (TextView) rightSide.findViewById(R.id.user_name);
            TextView lastSeen = (TextView) rightSide.findViewById(R.id.last_seen);
            name.setText(userRow.getFirstName());
            lastSeen.setText(userRow.getLastSeenHumanReadable());
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
