package com.mobilecomputing.englishtoromance.api

class WordsRepository (private val wordsApi: WordsApi){

    suspend fun getWords(): List<String> {
        return wordsApi.getWords().words
    }

    fun getRandomWords(): List<String> {
        return random_words
    }
}