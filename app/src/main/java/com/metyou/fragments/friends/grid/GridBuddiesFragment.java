package com.metyou.fragments.friends.grid;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.DateTime;
import com.metyou.R;
import com.metyou.cloud.services.model.SocialIdentity;
import com.metyou.cloud.services.model.UserEncountered;
import com.metyou.cloud.services.model.UsersBatch;
import com.metyou.cloud.services.model.UsersRequest;
import com.metyou.cloudapi.CloudApi;
import com.metyou.cloudapi.GetUsersTask;
import com.metyou.fragments.friends.ListRow;
import com.metyou.fragments.friends.LoaderRow;
import com.metyou.fragments.friends.UserAdapter;
import com.metyou.fragments.friends.UserRow;
import com.metyou.social.SocialProvider;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by mihai on 10/3/14.
 */
public class GridBuddiesFragment extends Fragment implements GetUsersTask.GetUsersCallback {

    private static final String TAG = "GridBuddiesFragment";
    private GridView userGridView;
    private UserAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Date lastRefreshDate;
    private boolean loaderSet;
    private Comparator<ListRow> mComparator;
    private ArrayList<ListRow> userList;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userList = new ArrayList<ListRow>();

        mComparator = new Comparator<ListRow>() {
            @Override
            public int compare(ListRow lhs, ListRow rhs) {
                if (lhs instanceof LoaderRow) {
                    return 1;
                } else if (rhs instanceof LoaderRow) {
                    return -1;
                } else {
                    UserRow luser = (UserRow)lhs;
                    UserRow ruser = (UserRow)rhs;
                    return 0 - luser.getLastSeen().compareTo(ruser.getLastSeen());
                }
            }
        };

//        adapter = new UserAdapter(getActivity(),
//                android.R.layout.simple_list_item_1,
//                userList,
//                imageFetcher);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.grid_buddies_fragment, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(TAG, "refresh");
                lastRefreshDate = new Date();
                UsersRequest usersRequest = new UsersRequest();
                usersRequest.setUserKey(SocialProvider.getId());
                usersRequest.setBeginningDate(new DateTime(lastRefreshDate));
                usersRequest.setCount(0);
                usersRequest.setOffset(0);
                requestUsers(usersRequest, GetUsersTask.RequestType.REFRESH);
            }
        });
        userGridView = (GridView) view.findViewById(R.id.buddy_grid);
        userGridView.setAdapter(adapter);
        return view;
    }

    private void requestUsers(UsersRequest ur, GetUsersTask.RequestType type) {
        CloudApi cloudApi = CloudApi.getCloudApi(null);
        cloudApi.getUsers(ur, type, this);
    }

    @Override
    public void onUsersLoaded(GetUsersTask.RequestType reqType, UsersBatch usersBatch) {
        if (usersBatch == null || usersBatch.getUsers() == null) {
            handleRequestType(reqType, true);
            return;
        }
        adapter.setNotifyOnChange(false);
        Log.d(TAG, usersBatch.getUsers().toString());

        for (UserEncountered ue : usersBatch.getUsers()) {
            UserRow userRow = new UserRow(ue);
            if (adapter.contains(userRow)) {
                Log.d(TAG, "found item " + userRow.getKey());
                adapter.update(userRow);
            } else {
                adapter.add(userRow);
            }
        }

        handleRequestType(reqType, usersBatch.getReachedEnd());
        adapter.sort(mComparator);
        adapter.notifyDataSetChanged();
    }


    private void handleRequestType(GetUsersTask.RequestType reqType, boolean reachedEnd) {
        if (reqType == GetUsersTask.RequestType.MORE) {
            if (!reachedEnd && !loaderSet) {
            } else if(reachedEnd && loaderSet) {
                loaderSet = false;
            }
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
            if (adapter.getCount() > 0) {
            } else {
            }
            if (!reachedEnd && !loaderSet) {
                loaderSet = true;
            } else if (reachedEnd) {
            }
        }
    }
}
