package brymian.bubbles.bryant.friends;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import brymian.bubbles.R;
import brymian.bubbles.bryant.nonactivity.SaveSharedPreference;
import brymian.bubbles.damian.nonactivity.ServerRequest.Callback.UserListCallback;
import brymian.bubbles.damian.nonactivity.ServerRequest.FriendshipStatusRequest;
import brymian.bubbles.objects.User;


public class FriendRequestSent extends Fragment{

    Toolbar mToolbar;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.friends_request_sent, container, false);
        mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.Sent_Friend_Requests);
        mToolbar.setTitleTextColor(Color.BLACK);

        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView_friend_request_sent);

        new FriendshipStatusRequest(getActivity()).getFriendshipStatusRequestSentUsers(SaveSharedPreference.getUserUID(getActivity()), "Friendship Pending", new UserListCallback() {
            @Override
            public void done(List<User> users) {
                List<FriendRequester> friendRequesterArrayList = new ArrayList<>();


                for (User user : users) {
                    FriendRequester friendRequester = new FriendRequester(user.getUsername(), user.getFirstName() + " " + user.getLastName(), user.getUid());
                    friendRequesterArrayList.add(friendRequester);
                }

                adapter = new FriendRequestSentRecyclerAdapter(getActivity(), friendRequesterArrayList);
                layoutManager = new LinearLayoutManager(getActivity());
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(adapter);

            }
        });
        return rootView;
    }
}