package com.moufee.boilerfit;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.telecom.Call;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.moufee.boilerfit.repository.UserRepository;
import com.moufee.boilerfit.ui.Communicator;
import com.moufee.boilerfit.util.Callback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;
import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

public class FriendsList extends Fragment {

    String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    // initialize images of all users
    ArrayList<User> users = new ArrayList<User>();
    ArrayList<User> tempUsers;

    Map<String, User> allUsers; // Doesnt contain users who are friends or who are sent friend request.
    Map<String, User> userFriends;
    Map<String, User> friendRequestSent;
    Map<String, User> friendRequestRecieved;
    int menuOption = 0; // 0 - user friends, 1 - , 2 -
    final CustomAdapter customAdapter = new CustomAdapter();
    SearchView searchFriends;

    @Inject
    UserRepository mUserRepository;
    ListView friendsList;
    int index = 0;

    // this is an interface to communicate with Main activity`
    // This allows us to communicate with the other fragment via the activity
    Communicator comm;

    public static FriendsList newInstance (){
        return new FriendsList();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        AndroidSupportInjection.inject(this);
        // to create notifications channel, let it be here


        // Step 1, we initiliaze the Communicator interface
        comm = (Communicator) getActivity();

        // Step 2, you can use this object to call any method in Main Activity
        // For now, I made a function "respond" to communicate with another Fragment
        // Now look at MainActivity.kt
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.friends_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_friends:
                menuOption = 2;
                customAdapter.notifyDataSetChanged();
                break;
            case R.id.pending_friends:
                menuOption = 1;
                customAdapter.notifyDataSetChanged();
                break;
            case R.id.my_friends:
                menuOption = 0;
                customAdapter.notifyDataSetChanged();
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_friends_list, container, false);

        friendsList = (ListView) rootView.findViewById(R.id.friendslist) ;
        searchFriends = (SearchView) rootView.findViewById(R.id.searchFriends);
        ImageView newFriends = (ImageView) rootView.findViewById(R.id.new_friends);
        registerForContextMenu(newFriends);

        customAdapter.removeFriendListener = new Callback<User>() {
            @Override
            public void accept(@Nullable User userFriend) {
                mUserRepository.getUserCallback(new Callback<User>() {
                    @Override
                    public void accept(@Nullable User user) {
                        userFriends.remove(userFriend.getUuid());
                        Map<String,String> userFriendsList = user.getFriends();
                        userFriendsList.remove(userFriend.getUuid());
                        user.setFriends(userFriendsList);

                        Map<String,String> userFriendFriendList = userFriend.getFriends();
                        userFriendFriendList.remove(user.getUuid());
                        userFriend.setFriends(userFriendFriendList);

                        mUserRepository.updateUser(user);
                        mUserRepository.updateUser(userFriend);
                        customAdapter.notifyDataSetChanged();

                    }
                });
            }
        };

        customAdapter.sendFriendRequestListener = new Callback<User>() {
            @Override
            public void accept(@Nullable User requestUser) {
                mUserRepository.getUserCallback(new Callback<User>() {
                    @Override
                    public void accept(@Nullable User user) {
                        Map<String,String> requestUserRequestMap = requestUser.getFriendRequestRecieved();
                        Map<String,String> userSendMap = user.getFriendRequestSent();
                        requestUserRequestMap.put(user.getUuid(),user.getUuid());
                        userSendMap.put(requestUser.getUuid(),requestUser.getUuid());
                        requestUser.setFriendRequestRecieved(requestUserRequestMap);
                        user.setFriendRequestSent(userSendMap);
                        mUserRepository.updateUser(user);
                        mUserRepository.updateUser(requestUser);
                        friendRequestSent.put(requestUser.getUuid(), requestUser);
                        allUsers.remove(requestUser.getUuid());
                        customAdapter.notifyDataSetChanged();
                    }
                });
            }
        };
        customAdapter.cancelFriendRequestListener = new Callback<User>() {
            @Override
            public void accept(@Nullable User requestUser) {
                mUserRepository.getUserCallback(new Callback<User>() {
                    @Override
                    public void accept(@Nullable User user) {
                        Map<String,String> requestUserRequestMap = requestUser.getFriendRequestRecieved();
                        Map<String,String> userSendMap = user.getFriendRequestSent();
                        requestUserRequestMap.remove(user.getUuid());
                        userSendMap.remove(requestUser.getUuid());

                        requestUser.setFriendRequestRecieved(requestUserRequestMap);
                        user.setFriendRequestSent(userSendMap);

                        mUserRepository.updateUser(user);
                        mUserRepository.updateUser(requestUser);

                        friendRequestSent.remove(requestUser.getUuid());
                        allUsers.put(requestUser.getUuid(), requestUser);
                        customAdapter.notifyDataSetChanged();
                    }
                });
            }
        };
        customAdapter.rejectRequestListener = new Callback<User>() {
            @Override
            public void accept(@Nullable User requestUser) {
                mUserRepository.getUserCallback(new Callback<User>() {
                    @Override
                    public void accept(@Nullable User user) {
                        Map<String, String> requestUserSentMap = requestUser.getFriendRequestSent();
                        Map<String, String> userReceivedMap  = user.getFriendRequestRecieved();
                        requestUserSentMap.remove(user.getUuid());
                        userReceivedMap.remove(requestUser.getUuid());

                        friendRequestRecieved.remove(requestUser.getUuid());
                        allUsers.put(requestUser.getUuid(), requestUser);

                        requestUser.setFriendRequestSent(requestUserSentMap);
                        user.setFriendRequestRecieved(userReceivedMap);
                        mUserRepository.updateUser(user);
                        mUserRepository.updateUser(requestUser);
                        customAdapter.notifyDataSetChanged();
                    }
                });
            }
        };

        customAdapter.acceptRequestListener = new Callback<User>() {
            @Override
            public void accept(@Nullable User requestUser) {
                mUserRepository.getUserCallback(new Callback<User>() {
                    @Override
                    public void accept(@Nullable User user) {
                        Map<String, String> requestUserSentMap = requestUser.getFriendRequestSent();
                        Map<String, String> userReceivedMap  = user.getFriendRequestRecieved();
                        requestUserSentMap.remove(user.getUuid());
                        userReceivedMap.remove(requestUser.getUuid());

                        friendRequestRecieved.remove(requestUser.getUuid());
                        userFriends.put(requestUser.getUuid(),requestUser);

                        Map<String, String> requestFriendsMap = requestUser.getFriends();
                        Map<String, String> userFriendsMap = user.getFriends();
                        requestFriendsMap.put(user.getUuid(), user.getUuid());
                        userFriendsMap.put(requestUser.getUuid(), requestUser.getUuid());

                        user.setFriends(userFriendsMap);
                        requestUser.setFriends(requestFriendsMap);
                        user.setFriendRequestRecieved(userReceivedMap);
                        requestUser.setFriendRequestSent(requestUserSentMap);

                        mUserRepository.updateUser(user);
                        mUserRepository.updateUser(requestUser);
                        customAdapter.notifyDataSetChanged();
                    }
                });
            }
        };
        mUserRepository.getUserFriendsFromIDCallback(new Callback<HashMap<String, User>>() {
            @Override
            public void accept(@Nullable HashMap<String, User> stringUserHashMap) {
                userFriends = stringUserHashMap;
                mUserRepository.getUserFriendRequestSentFromIDCallback(new Callback<HashMap<String, User>>() {
                    @Override
                    public void accept(@Nullable HashMap<String, User> stringUserHashMap) {
                        friendRequestSent = stringUserHashMap;
                        mUserRepository.getUserFriendRecievedSentFromIDCallback(new Callback<HashMap<String, User>>() {
                            @Override
                            public void accept(@Nullable HashMap<String, User> stringUserHashMap) {
                                friendRequestRecieved = stringUserHashMap;
                                mUserRepository.getListUserCallback(new Callback<HashMap<String, User>>() {
                                    @Override
                                    public void accept(@Nullable HashMap<String, User> stringUserHashMap) {
                                        String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                        User currentUser = stringUserHashMap.get(currentUserID);
                                        allUsers = new HashMap<>();
                                        for(String userID : stringUserHashMap.keySet()){
                                            if(!userID.equals(currentUserID) && !userFriends.containsKey(userID) && !friendRequestSent.containsKey(userID) && !friendRequestRecieved.containsKey(userID)){
                                                allUsers.put(userID, stringUserHashMap.get(userID));
                                            }
                                        }
                                        users = new ArrayList<User>(stringUserHashMap.values());
                                        tempUsers = new ArrayList<User>(users);
                                        friendsList.setAdapter(customAdapter);
                                    }
                                });
                            }
                        }, currentUserID);
                    }
                }, currentUserID);
            }
        }, currentUserID);

        // Inflate the layout for this fragment

        searchFriends.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if(index == 0){
                    Toast.makeText(getActivity(), "No Results Found", Toast.LENGTH_LONG).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) { // this function will be called for every entry

                index = 0;
                if(!newText.isEmpty()) {
                    try {
                        tempUsers = new ArrayList<User>(users);
                        for(int i=tempUsers.size();i>0;i--){
                            if (!tempUsers.get(i - 1).getName().toLowerCase().contains(newText.toLowerCase())) {
                                tempUsers.remove(i-1);
                            }
                        }
                        customAdapter.notifyDataSetChanged();
                    }
                    catch(Exception e){
                        tempUsers = new ArrayList<User>(users);
                         customAdapter.notifyDataSetChanged();

                    }
                }else{
                    tempUsers = new ArrayList<User>(users);
                    customAdapter.notifyDataSetChanged();
                }


                return false;
            }
        });

        searchFriends.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {

                return false;
            }
        });

        return rootView;
    }

    class CustomAdapter extends BaseAdapter {
        private Callback<User> removeFriendListener;
        private Callback<User> sendFriendRequestListener;
        private Callback<User> cancelFriendRequestListener;
        private Callback<User> acceptRequestListener;
        private Callback<User> rejectRequestListener;


        @Override
        public int getCount() {
            if(menuOption == 0){
                return userFriends.keySet().size();
            }
            else if(menuOption == 2){
                return allUsers.size() + friendRequestSent.size();
            }
            else if(menuOption == 1){
                return friendRequestRecieved.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            TextView name = null;
            Button actionButton = null;
            ImageView profilePicture = null;
            ImageButton secondActionButton = null;
            User cardUser = null;
            // in case of all users/ pending requests use card_item. For my friends use friends_card_item to inflate:
            //convertView = getLayoutInflater().inflate(R.layout.friend_card_item,null);

            if(menuOption == 0){
                searchFriends.setVisibility(View.INVISIBLE);
                convertView = getLayoutInflater().inflate(R.layout.friend_card_item,null);
                profilePicture = (ImageView) convertView.findViewById(R.id.profilePictureFriendsList);
                name = (TextView) convertView.findViewById(R.id.displayName);
                actionButton = (Button) convertView.findViewById(R.id.editFriend);
                secondActionButton = (ImageButton) convertView.findViewById(R.id.navigatetofriend);

                cardUser =  userFriends.get(userFriends.keySet().toArray()[position]);
                final User finalUser = cardUser;
                actionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        removeFriendListener.accept(finalUser);
                        Toast.makeText(getActivity(),"Friend Removed", Toast.LENGTH_LONG).show();
                    }
                });

                secondActionButton.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // navigate to friend's profile page
                        FriendsProfilePicture friendsProfilePicture = new FriendsProfilePicture();
                        friendsProfilePicture.setDate(finalUser.getName(), finalUser.getBio(), finalUser.getUuid(),3,3);
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.friendslists, friendsProfilePicture);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }
                });
            }else if(menuOption == 1){
                searchFriends.setVisibility(View.INVISIBLE);
                convertView = getLayoutInflater().inflate(R.layout.friend_card_item,null);
                profilePicture = (ImageView) convertView.findViewById(R.id.profilePictureFriendsList);
                name = (TextView) convertView.findViewById(R.id.displayName);
                actionButton = (Button) convertView.findViewById(R.id.editFriend);
                secondActionButton = (ImageButton) convertView.findViewById(R.id.navigatetofriend);
                String keyString = (String)friendRequestRecieved.keySet().toArray()[position];
                cardUser = friendRequestRecieved.get(keyString);
                final User finalUser = cardUser;
                actionButton.setBackgroundResource(R.drawable.add_friend);
                secondActionButton.setBackgroundResource(R.drawable.remove_friend);
                actionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        acceptRequestListener.accept(finalUser);
                        Toast.makeText(getActivity(),"Friend Added", Toast.LENGTH_LONG).show();
                    }
                });
                secondActionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        rejectRequestListener.accept(finalUser);
                        Toast.makeText(getActivity(),"Friend request rejected", Toast.LENGTH_LONG).show();
                    }
                });

            } else if(menuOption == 2){
                searchFriends.setVisibility(View.INVISIBLE);
                convertView = getLayoutInflater().inflate(R.layout.card_item,null);
                profilePicture = (ImageView) convertView.findViewById(R.id.profilePictureFriendsList);
                name = (TextView) convertView.findViewById(R.id.displayName);
                actionButton = (Button) convertView.findViewById(R.id.editFriend);
                if(position >= friendRequestSent.size()){
                    actionButton.setBackgroundResource(R.drawable.add_friend);
                    String keyString = (String)allUsers.keySet().toArray()[position - friendRequestSent.size()];
                    cardUser = allUsers.get(keyString);
                    final User finalUser = cardUser;

                    actionButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            sendFriendRequestListener.accept(finalUser);
                            Toast.makeText(getActivity(),"Friend Request Sent", Toast.LENGTH_LONG).show();
                        }
                    });
                }else{
                    actionButton.setBackgroundResource(R.drawable.remove_friend);
                    String keyString = (String)friendRequestSent.keySet().toArray()[position];
                    cardUser = friendRequestSent.get(keyString);
                    final User finalUser = cardUser;
                    actionButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            cancelFriendRequestListener.accept(finalUser);
                        }
                    });
                }
            }

            final User finalUser = cardUser;
            name.setText(cardUser.getName());
            profilePicture.setImageResource(R.drawable.profile);
            return convertView;
        }
    }
}
