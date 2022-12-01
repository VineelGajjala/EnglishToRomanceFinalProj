package com.mobilecomputing.englishtoromance.api

class WordsRepository (private val wordsApi: WordsApi){

    fun getWords(): List<String> {
        return wordsApi.getWords().words
    }
}