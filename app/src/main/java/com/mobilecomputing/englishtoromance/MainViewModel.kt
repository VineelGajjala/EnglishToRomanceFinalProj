package com.mobilecomputing.englishtoromance

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainViewModel : ViewModel() {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val dbHelp = ViewModelDBHelper()
    private var flashCardMetaList = MutableLiveData<List<FlashCardMeta>>()

    fun updateUser() {

    }

    fun removeFlashCard(position: Int) {
        val arr = flashCardMetaList.value!!.toMutableList()
        var removedFlashCardMetaData = arr.removeAt(position)
        flashCardMetaList.value = arr.toList()

        dbHelp.removePhotoMeta(removedFlashCardMetaData, flashCardMetaList)
    }

    fun signOut() {
        FirebaseAuth.getInstance().signOut()
    }
    fun getCurrentUser() : FirebaseUser? {
        return firebaseAuth.currentUser
    }
    fun createFlashCardMeta(englishWord: String, spanishWord : String, uuid : String) {
        val currentUser = getCurrentUser()!!
        val flashCardMeta = FlashCardMeta(
            ownerName = currentUser.displayName,
            ownerUid = currentUser.uid,
            uuid = uuid,
            englishWord = englishWord,
            spanishWord = spanishWord
        )

        dbHelp.createPhotoMeta(flashCardMeta, flashCardMetaList)
    }
    fun fetchflashCards() {
        dbHelp.fetchPhotoMeta(flashCardMetaList)
//        Log.d("XXX", flashCardMetaList.value!!.size.toString())
    }
    fun observeflashCards(): LiveData<List<FlashCardMeta>> {
        return flashCardMetaList
    }
    fun getflashCards():List<FlashCardMeta> {
        return flashCardMetaList.value!!
    }
}