package com.metyou.fragments;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.metyou.R;
import com.metyou.social.User;

import java.util.ArrayList;

public class BuddiesFragment extends Fragment implements User.UpdateListener{

    ListView userListView;
    UserListAdapter arrayAdapter;
    ArrayList<User> userList;
    Bitmap userPhoto;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userList = new ArrayList<User>();
        userList.add(new User("00", null));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);
        arrayAdapter = new UserListAdapter(getActivity(), android.R.layout.simple_list_item_1, userList);
        userListView = (ListView)view.findViewById(R.id.buddy_list);
        userListView.setAdapter(arrayAdapter);
        return view;
    }

    public void addUser(String userId) {
        userList.add(new User(userId, this));
    }


    @Override
    public void updateUserList() {
        arrayAdapter.notifyDataSetChanged();
    }
}
