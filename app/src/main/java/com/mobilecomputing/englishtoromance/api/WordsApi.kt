package com.mobilecomputing.englishtoromance.api


import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

data class WordsList (val words: List<String>)
interface WordsApi {
    @GET("data")
    suspend fun getWords() : WordsList

    /**
     * Factory class for convenient creation of the Api Service interface
     */
    companion object Factory {
        fun create(): WordsApi {
            val retrofit: Retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                // Must end in /!
                .baseUrl("https://www.randomlists.com/data/words.json/")
                .build()
            return retrofit.create(WordsApi::class.java)
        }
    }
}