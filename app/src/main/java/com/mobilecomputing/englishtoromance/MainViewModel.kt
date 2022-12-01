package edu.utap.firebaseauth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mobilecomputing.englishtoromance.FlashCard
import com.mobilecomputing.englishtoromance.flashCardData

class MainViewModel : ViewModel() {
    private var allFlashCards = MutableLiveData<List<flashCardData>>()
    private var favoritedFlashCards = MutableLiveData<List<flashCardData>>()
    private var currentFlashCard = MutableLiveData<flashCardData>()
    private var currentIndex = MutableLiveData<Int>()

    fun setInitialValues() {
        var hiFlash = flashCardData("Hola", "Hello")
        var flashCards = listOf(hiFlash,
            flashCardData("Hoy", "Here"),
            flashCardData("Adios", "Bye"),
            flashCardData("Donde", "Where"),
            flashCardData("Ahora", "Right now"))

        allFlashCards.value = flashCards
        favoritedFlashCards.value = listOf(hiFlash)
        currentFlashCard.value = hiFlash
        currentIndex.value = 0
    }

    fun observeAllFlashCards(): MutableLiveData<List<flashCardData>> {
        return allFlashCards
    }

    fun observeFavoritedFlashCards(): MutableLiveData<List<flashCardData>> {
        return favoritedFlashCards
    }

    fun observeCurrentFlashCard(): MutableLiveData<flashCardData> {
        return currentFlashCard
    }

    fun observeIndex(): MutableLiveData<Int> {
        return currentIndex
    }

    fun increaseIndex() {
        var x = currentIndex.value!! + 1
        if (x >= allFlashCards.value!!.size) {
            x = 0
        }
        currentIndex.value = x
        currentFlashCard.value = allFlashCards.value!![currentIndex.value!!]
    }

    fun decreaseIndex() {
        var x = currentIndex.value!! - 1
        if (x < 0) {
            x = allFlashCards.value!!.size - 1
        }
        currentIndex.value = x
        currentFlashCard.value = allFlashCards.value!![currentIndex.value!!]
    }

    fun addToAll(englishWord : String, spanishWord : String) {
        var value = allFlashCards.value!!.toMutableList()
        var newFlash = flashCardData(englishWord, spanishWord)
        value.add(newFlash)
        allFlashCards.value = value.toList()
    }

    fun addToFavorites() {
        var value = favoritedFlashCards.value!!.toMutableList()
        value.add(currentFlashCard.value!!)
        favoritedFlashCards.value = value.toList()
    }

    fun removeFavorite() {
        var value = favoritedFlashCards.value!!.toMutableList()
        value.remove(currentFlashCard.value!!)
        favoritedFlashCards.value = value.toList()
    }

    fun getFavorited(): List<flashCardData> {
        return favoritedFlashCards.value!!
    }

}