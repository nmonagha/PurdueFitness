package com.moufee.boilerfit.util;

import android.arch.lifecycle.LiveData;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

/**
 * Created by Ben on 3/10/18.
 */

public class FirestoreDocumentLiveData extends LiveData<DocumentSnapshot> {

    private DocumentReference mDocumentReference;

    private EventListener<DocumentSnapshot> mListener = new DocumentListener();

    private ListenerRegistration mRegistration;


    public FirestoreDocumentLiveData(DocumentReference documentReference) {
        mDocumentReference = documentReference;
    }

    @Override
    protected void onActive() {
        mRegistration = mDocumentReference.addSnapshotListener(mListener);
    }

    @Override
    protected void onInactive() {
        if (mRegistration != null)
            mRegistration.remove();
    }

    private class DocumentListener implements EventListener<DocumentSnapshot> {
        @Override
        public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
            setValue(documentSnapshot);
        }
    }
}
