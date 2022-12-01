package com.mobilecomputing.englishtoromance.api

class WordsRepository (private val wordsApi: WordsApi){

    suspend fun getWords(): List<String> {
        return wordsApi.getWords().words
    }
}