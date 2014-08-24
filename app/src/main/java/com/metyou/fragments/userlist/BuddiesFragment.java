package com.metyou.fragments.userlist;


import android.app.Activity;
import android.app.Fragment;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.facebook.AccessToken;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestBatch;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphObject;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.metyou.R;
import com.metyou.cloud.services.model.SocialIdentity;
import com.metyou.cloud.services.model.UserEncountered;
import com.metyou.cloud.services.model.UsersBatch;
import com.metyou.cloudapi.CloudApi;
import com.metyou.cloudapi.GetUsersTask;
import com.metyou.social.SocialProvider;
import com.metyou.social.User;
import com.metyou.util.ImageCache;
import com.metyou.util.ImageFetcher;

import org.json.JSONException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

public class BuddiesFragment extends Fragment implements EndlessScrollListener.EndlessScrollCallback,
        GetUsersTask.GetUsersCallback {

    private static final String TAG = "BuddiesFragment";
    private static final String IMAGE_CACHE_DIR = "thumbs";
    ListView userListView;
    UserListAdapter arrayAdapter;
    ArrayList<ListRow> userList;
    EndlessScrollListener endlessScrollListener;
    Date lastDate;
    private ImageFetcher imageFetcher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userList = new ArrayList<ListRow>();

//        Session.getActiveSession().closeAndClearTokenInformation();
//        Session.setActiveSession(Session.openActiveSessionWithAccessToken(getActivity(),
//                AccessToken.createFromExistingAccessToken("CAAD0oAZC50loBAKF8AlRBo4ed0vRwnBd8rmMRsCxqR66CaOKp4bF0AhTBzppAGyVsUHC08iIX7a6SBnutI0FZBXhZBUKTQ1decY5ZAWQk2dg7VEaCoNTfBlemVtUQxI3N9bZA6gDm5y1YWYC6iYXK3DTdTqqrXa8yQXO20VeEwLtSePHqfzrKE4YiKUtAY7YUJZB02sHGuowI6hQ2RS6KKZAtZASuPrbv7YZD",
//                        null, null, null, null), null));

        ImageCache.ImageCacheParams cacheParams =
                new ImageCache.ImageCacheParams(getActivity(), IMAGE_CACHE_DIR);
        cacheParams.setMemCacheSizePercent(0.25f);
        imageFetcher = new ImageFetcher(getActivity(), 60, 60);
        imageFetcher.addImageCache(getFragmentManager(), cacheParams);

        for (int i = 0; i < 10; i++) {
            UserEncountered ue = new UserEncountered();
            ue.setFirstName("sample");
            ue.setSocialId("me");
            userList.add(new UserRow(ue));
        }
        arrayAdapter = new UserListAdapter(getActivity(),
                android.R.layout.simple_list_item_1,
                userList,
                imageFetcher);
        endlessScrollListener = new EndlessScrollListener(this);
        lastDate = new Date();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.buddies_fragment, container, false);
        userListView = (ListView)view.findViewById(R.id.buddy_list);
        userListView.setOnScrollListener(endlessScrollListener);
        userListView.setAdapter(arrayAdapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        imageFetcher.setExitTasksEarly(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        imageFetcher.setPauseWork(false);
        imageFetcher.setExitTasksEarly(true);
        imageFetcher.flushCache();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        imageFetcher.closeCache();
    }

    @Override
    public void onBottom() {
        SocialProvider.readPreferences(getActivity());
        SocialIdentity socialIdentity = SocialProvider.getSocialIdentity();

        GoogleAccountCredential credential = GoogleAccountCredential.usingAudience(
                getActivity(), CloudApi.AUDIENCE);
        credential.setSelectedAccountName(socialIdentity.getEmail());
        CloudApi cloudApi = CloudApi.getCloudApi(credential);
        cloudApi.getUsers(SocialProvider.getId(getActivity().getApplicationContext()),
                new Date(),
                this);
    }

    @Override
    public void onUserLoaded(UsersBatch usersBatch) {
        if (usersBatch == null) {
            return;
        }
        Log.d(TAG, "loaded " + usersBatch.getUsers().size() + " users");

        ListRow lastRow = arrayAdapter.getItem(arrayAdapter.getCount() - 1);
        if (lastRow instanceof LoaderRow) {
            arrayAdapter.remove(lastRow);
        }
        for (UserEncountered ue : usersBatch.getUsers()) {
            final UserRow userRow = new UserRow(ue);
            arrayAdapter.add(userRow);
        }

        if (!usersBatch.getReachedEnd()) {
            arrayAdapter.add(new LoaderRow());
            Log.d(TAG, "Added Loader");
        } else {
            endlessScrollListener.performTaskOnBottom(false);
        }
        endlessScrollListener.onBottomTaskFinished();
    }
}
