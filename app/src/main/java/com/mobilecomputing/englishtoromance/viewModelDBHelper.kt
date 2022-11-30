package com.mobilecomputing.englishtoromance

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ViewModelDBHelper() {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val rootCollection = "allStarredWords"

    fun fetchPhotoMeta(notesList: MutableLiveData<List<FlashCardMeta>>) {
        dbFetchPhotoMeta(notesList)
    }
    // If we want to listen for real time updates use this
    // .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
    // But be careful about how listener updates live data
    // and noteListener?.remove() in onCleared
    private fun limitAndGet(query: Query,
                            notesList: MutableLiveData<List<FlashCardMeta>>) {
        query
            .limit(100)
            .get()
            .addOnSuccessListener { result ->
                Log.d(javaClass.simpleName, "allNotes fetch ${result!!.documents.size}")
                // NB: This is done on a background thread
                notesList.postValue(result.documents.mapNotNull {
                    it.toObject(FlashCardMeta::class.java)
                })
            }
            .addOnFailureListener {
                Log.d(javaClass.simpleName, "allNotes fetch FAILED ", it)
            }
    }
    //////////////////////////////////////////////createPh///////////////
    // Interact with Firestore db
    // https://firebase.google.com/docs/firestore/query-data/order-limit-data
    private fun dbFetchPhotoMeta(notesList: MutableLiveData<List<FlashCardMeta>>) {
        // XXX Write me and use limitAndGet
        limitAndGet(db.collection(rootCollection), notesList)
    }

    // https://firebase.google.com/docs/firestore/manage-data/add-data#add_a_document
    fun createPhotoMeta(
        photoMeta: FlashCardMeta,
        notesList: MutableLiveData<List<FlashCardMeta>>
    ) {
        // You can get a document id if you need it.
        photoMeta.firestoreID = db.collection(rootCollection).document().id
        // XXX Write me: add photoMeta
        db.collection(rootCollection).add(photoMeta)
            .addOnSuccessListener {
                dbFetchPhotoMeta(notesList)
            }
    }

    // https://firebase.google.com/docs/firestore/manage-data/delete-data#delete_documents
    fun removePhotoMeta(
        photoMeta: FlashCardMeta,
        photoMetaList: MutableLiveData<List<FlashCardMeta>>
    ) {
        // XXX Write me.  Make sure you delete the correct entry
        db.collection(rootCollection).document(photoMeta.firestoreID).delete()
            .addOnSuccessListener {
                dbFetchPhotoMeta(photoMetaList)
            }
    }
}
