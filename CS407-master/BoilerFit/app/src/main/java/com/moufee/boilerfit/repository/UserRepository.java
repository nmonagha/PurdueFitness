package com.moufee.boilerfit.repository;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.moufee.boilerfit.User;
import com.moufee.boilerfit.util.Callback;
import com.moufee.boilerfit.util.FirestoreDocumentLiveData;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class UserRepository {
    private FirebaseFirestore mFirebaseFirestore;

    private CollectionReference mUsersCollection;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseStorage mFirebaseStorage;

    @Inject
    public UserRepository(FirebaseFirestore firebaseFirestore, FirebaseAuth firebaseAuth, FirebaseStorage firebaseStorage) {
        mFirebaseFirestore = firebaseFirestore;
        mFirebaseAuth = firebaseAuth;
        mFirebaseStorage = firebaseStorage;
        mUsersCollection = mFirebaseFirestore.collection("users");
    }

    public void userExist(final Callback<Boolean> callback) {
        String uId = mFirebaseAuth.getCurrentUser().getUid();
        mUsersCollection.document(uId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        callback.accept(true);
                    } else {
                        callback.accept(false);
                    }
                } else {
                    callback.accept(false);
                }
            }
        });

    }

    public void updatePassword(String newPassword) {
        if (newPassword != null) {
            FirebaseUser user = mFirebaseAuth.getCurrentUser();
            if (user != null) {
                user.updatePassword(newPassword);
            }
        }
    }

    public LiveData<User> getUser(){
        String uid = mFirebaseAuth.getCurrentUser().getUid();
        return Transformations.map(new FirestoreDocumentLiveData(mUsersCollection.document(uid)), new Function<DocumentSnapshot, User>() {
            @Override
            public User apply(DocumentSnapshot input) {
                if (input == null || !input.exists()) return null;
                return input.toObject(User.class);
            }
        });
    }

    public void getUserCallback(final Callback<User> callback) {
        mUsersCollection.document(mFirebaseAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                callback.accept(documentSnapshot.toObject(User.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.accept(null);
            }
        });
    }

    public void getUserCallbackFromID(final Callback<User> callback, String id) {
        mUsersCollection.document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                callback.accept(documentSnapshot.toObject(User.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.accept(null);
            }
        });
    }

    public void getListUserCallback(final Callback<HashMap<String, User>> callback) {
        mUsersCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    HashMap<String, User> map= new HashMap<String, User>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        map.put(document.getId(),document.toObject(User.class));
                    }
                    callback.accept(map);
                } else {
                    callback.accept(null);
                }
            }
        });
    }

    public void getHealthyMap(final Callback<HashMap<String, Boolean>> callback){
        CollectionReference healthyCollection  = mFirebaseFirestore.collection("healthy");
        healthyCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    HashMap<String, Boolean> healthyMap = new HashMap<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
//                        Log.d(TAG, document.getId() + " => " + document.getData());
                        long isHealthy = (long)document.getData().get("isHealthy");
                        boolean bol_isHealthy = true;
                        if (isHealthy == 0) {
                            bol_isHealthy = false;
                        }
                        String documentId = document.getId();
                        healthyMap.put(documentId,bol_isHealthy);
                    }
                    callback.accept(healthyMap);

                }  else{
                    callback.accept(new HashMap<String, Boolean>());
                }
            }
        });
    }

    public void getUserFriendsFromIDCallback(final Callback<HashMap<String, User>> callback, String id){
        getListUserCallback(new Callback<HashMap<String, User>>() {
            @Override
            public void accept(@Nullable HashMap<String, User> stringUserHashMap) {
                String userID = mFirebaseAuth.getCurrentUser().getUid();
                User currentUser= stringUserHashMap.get(userID);
                Map<String, String> userFriendsID = currentUser.getFriends();
                HashMap<String, User>userFriends = new HashMap<>();

                for(String friendID : userFriendsID.keySet()){
                    if(stringUserHashMap.containsKey(friendID))
                        userFriends.put(friendID, stringUserHashMap.get(friendID));
                }
                callback.accept(userFriends);
            }
        });
    }

    public void getUserFriendRequestSentFromIDCallback(final Callback<HashMap<String, User>> callback, String id){
        getListUserCallback(new Callback<HashMap<String, User>>() {
            @Override
            public void accept(@Nullable HashMap<String, User> stringUserHashMap) {
                String userID = mFirebaseAuth.getCurrentUser().getUid();
                User currentUser= stringUserHashMap.get(userID);
                Map<String, String> userFriendRequestSentID = currentUser.getFriendRequestSent();
                HashMap<String, User>userFriendRequestSent = new HashMap<>();

                for(String friendID : userFriendRequestSentID.keySet()){
                    if(stringUserHashMap.containsKey(friendID))
                        userFriendRequestSent.put(friendID, stringUserHashMap.get(friendID));
                }
                callback.accept(userFriendRequestSent);
            }
        });
    }

    public void getUserFriendRecievedSentFromIDCallback(final Callback<HashMap<String, User>> callback, String id){
        getListUserCallback(new Callback<HashMap<String, User>>() {
            @Override
            public void accept(@Nullable HashMap<String, User> stringUserHashMap) {
                String userID = mFirebaseAuth.getCurrentUser().getUid();
                User currentUser= stringUserHashMap.get(userID);
                Map<String, String> userFriendRequestReceivedID = currentUser.getFriendRequestRecieved();
                HashMap<String, User>userFriendRequestRecieved = new HashMap<>();

                for(String friendID : userFriendRequestReceivedID.keySet()){
                    if(stringUserHashMap.containsKey(friendID))
                        userFriendRequestRecieved.put(friendID, stringUserHashMap.get(friendID));
                }
                callback.accept(userFriendRequestRecieved);
            }
        });
    }
    
    public void updateUser(User user){
        mUsersCollection.document(user.getUuid()).set(user);
    }

    public void createUser(User user, final Callback<Boolean> callback) {
        String currentUId = mFirebaseAuth.getCurrentUser().getUid();
        user.setUuid(currentUId);
        user.setEmail(mFirebaseAuth.getCurrentUser().getEmail());
        user.setName(mFirebaseAuth.getCurrentUser().getDisplayName());
        user.setFriends(new HashMap<>());
        user.setFriendRequestRecieved(new HashMap<>());
        user.setFriendRequestSent(new HashMap<>());

        mUsersCollection.document(user.getUuid()).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                callback.accept(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.accept(false);
            }
        });
    }
}
