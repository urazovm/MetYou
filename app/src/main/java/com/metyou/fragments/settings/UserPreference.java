package com.metyou.fragments.settings;

import android.content.Context;
import android.content.Intent;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.metyou.MainActivity;
import com.metyou.R;
import com.metyou.UserPhotos;
import com.metyou.social.SocialProvider;
import com.metyou.util.ImageFetcher;

/**
 * Created by mihai on 10/4/14.
 */
public class UserPreference extends Preference {
    private ImageFetcher imageFetcher;

    public UserPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayoutResource(R.layout.preference_user);
        imageFetcher = ((MainActivity)context).getImageFetcher();
    }

    public UserPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setLayoutResource(R.layout.preference_user);
        imageFetcher = ((MainActivity)context).getImageFetcher();
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        ImageView userImage = (ImageView) view.findViewById(R.id.settings_user_photo);
        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), UserPhotos.class);
                intent.putExtra("socialId", SocialProvider.getFacebookId());
                intent.putExtra("firstName", SocialProvider.getFirstName());
                getContext().startActivity(intent);
            }
        });
        TextView textView = (TextView) view.findViewById(R.id.settings_user_name);
        imageFetcher.loadProfileFBImage(SocialProvider.getFacebookId(), userImage);
        textView.setText(SocialProvider.getFirstName() + " " + SocialProvider.getLastName());
    }
}
