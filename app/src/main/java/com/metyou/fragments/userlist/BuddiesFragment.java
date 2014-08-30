package com.metyou.fragments.userlist;


import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.DateTime;
import com.metyou.R;
import com.metyou.cloud.services.model.SocialIdentity;
import com.metyou.cloud.services.model.UserEncountered;
import com.metyou.cloud.services.model.UsersBatch;
import com.metyou.cloud.services.model.UsersRequest;
import com.metyou.cloudapi.CloudApi;
import com.metyou.cloudapi.GetUsersTask;
import com.metyou.fragments.userlist.refreshner.SwipeRefreshLayout;
import com.metyou.social.SocialProvider;
import com.metyou.util.ImageCache;
import com.metyou.util.ImageFetcher;

import java.util.ArrayList;
import java.util.Date;

public class BuddiesFragment extends Fragment implements EndlessScrollListener.EndlessScrollCallback,
        GetUsersTask.GetUsersCallback {

    private static final String TAG = "BuddiesFragment";
    private static final String IMAGE_CACHE_DIR = "thumbs";
    private static final int AMOUNT_TO_LOAD = 10;
    private ListView userListView;
    private UserListAdapter arrayAdapter;
    private ArrayList<ListRow> userList;
    private EndlessScrollListener endlessScrollListener;

    private ImageFetcher imageFetcher;

    private SwipeRefreshLayout swipeRefreshLayout;
    private Date lastRefreshDate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userList = new ArrayList<ListRow>();

        ImageCache.ImageCacheParams cacheParams =
                new ImageCache.ImageCacheParams(getActivity(), IMAGE_CACHE_DIR);
        cacheParams.setMemCacheSizePercent(0.25f);
        imageFetcher = new ImageFetcher(getActivity(), 60, 60);
        imageFetcher.addImageCache(getFragmentManager(), cacheParams);

        arrayAdapter = new UserListAdapter(getActivity(),
                android.R.layout.simple_list_item_1,
                userList,
                imageFetcher);
        endlessScrollListener = new EndlessScrollListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.buddies_fragment, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(TAG, "refresh");
                lastRefreshDate = new Date();
                UsersRequest usersRequest = new UsersRequest();
                usersRequest.setUserKey(SocialProvider.getId());
                usersRequest.setBeginningDate(new DateTime(lastRefreshDate));
                usersRequest.setCount(AMOUNT_TO_LOAD);
                usersRequest.setOffset(0);
                requestUsers(usersRequest, GetUsersTask.RequestType.REFRESH);
            }
        });
        userListView = (ListView)view.findViewById(R.id.buddy_list);
        userListView.setOnScrollListener(endlessScrollListener);
        userListView.setAdapter(arrayAdapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        imageFetcher.setExitTasksEarly(false);
        if (arrayAdapter.isEmpty()) {
            lastRefreshDate = new Date();
            UsersRequest usersRequest = new UsersRequest();
            usersRequest.setUserKey(SocialProvider.getId());
            usersRequest.setBeginningDate(new DateTime(lastRefreshDate));
            usersRequest.setCount(AMOUNT_TO_LOAD);
            usersRequest.setOffset(0);
            requestUsers(usersRequest, GetUsersTask.RequestType.EMPTY);
        }
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
        Log.d(TAG, "on Bottom");
        UsersRequest usersRequest = new UsersRequest();
        usersRequest.setUserKey(SocialProvider.getId());
        usersRequest.setBeginningDate(new DateTime(lastRefreshDate));
        usersRequest.setCount(AMOUNT_TO_LOAD);
        usersRequest.setOffset(arrayAdapter.getCount());
        requestUsers(usersRequest, GetUsersTask.RequestType.MORE);
    }

    @Override
    public void onUsersLoaded(GetUsersTask.RequestType reqType, UsersBatch usersBatch) {
        if (usersBatch == null || usersBatch.getUsers() == null) {
            handleRequestType(reqType);
            return;
        }

        Log.d(TAG, "loaded " + usersBatch.getUsers().size() + " users");

        if (arrayAdapter.getCount() != 0) {
            ListRow lastRow = arrayAdapter.getItem(arrayAdapter.getCount() - 1);
            if (lastRow instanceof LoaderRow) {
                arrayAdapter.remove(lastRow);
            }
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

        handleRequestType(reqType);
    }

    private void handleRequestType(GetUsersTask.RequestType reqType) {
        if (reqType == GetUsersTask.RequestType.MORE) {
            endlessScrollListener.onBottomTaskFinished();
        } else if (reqType == GetUsersTask.RequestType.REFRESH) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    if (swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
            });
        } else {
            if (arrayAdapter.getCount() > 0) {
                swipeRefreshLayout.setActive(true);
            } else {
                swipeRefreshLayout.setActive(false);
            }
        }
    }

    private void requestUsers(UsersRequest ur, GetUsersTask.RequestType type) {
        SocialIdentity socialIdentity = SocialProvider.getSocialIdentity();
        GoogleAccountCredential credential = GoogleAccountCredential.usingAudience(
                getActivity(), CloudApi.AUDIENCE);
        credential.setSelectedAccountName(socialIdentity.getEmail());
        CloudApi cloudApi = CloudApi.getCloudApi(credential);
        cloudApi.getUsers(ur, type, this);
    }
}
