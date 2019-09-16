package com.moufee.boilerfit.util;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.function.Consumer;

public final class UserUtil {

    private static final String userProfilePhotosCollectionKey = "Profile Pictures";

    public static void getProfilePicture(final Consumer<Uri> callback) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            callback.accept(null);
        }
        String uuid = user.getUid();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference mystorage = storage.getReference().child(userProfilePhotosCollectionKey).child(uuid);
        mystorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                callback.accept(uri);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                callback.accept(null);
            }
        });
    }

    public static void getProfilePictureForUser(final Consumer<Uri> callback, String id) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            callback.accept(null);
        }
        String uuid = id;
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference mystorage = storage.getReference().child(userProfilePhotosCollectionKey).child(uuid);
        mystorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                callback.accept(uri);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                callback.accept(null);
            }
        });
    }

    public static void setProfilePicture(final Uri uri, final Consumer<Uri> callback) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            callback.accept(null);
        }
        String uuid = user.getUid();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference mystorage = storage.getReference().child(userProfilePhotosCollectionKey).child(uuid);
        mystorage.putFile(uri).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                callback.accept(null);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                UserUtil.getProfilePicture(callback);
            }
        });
    }

    public static void updatePassword(String password, final Consumer<Boolean> callback) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            callback.accept(null);
        }
        user.updatePassword(password)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            callback.accept(true);
                            return;
                        }
                        callback.accept(false);
                    }
                });
    }


}