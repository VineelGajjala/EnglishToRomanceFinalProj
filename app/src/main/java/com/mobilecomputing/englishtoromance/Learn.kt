package com.mobilecomputing.englishtoromance

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions
import com.mobilecomputing.englishtoromance.databinding.ActivityLearnBinding


class Learn : AppCompatActivity() {

    var englishSpanishTranslator: FirebaseTranslator? = null
    private lateinit var learnBinding: ActivityLearnBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learn)
        learnBinding = ActivityLearnBinding.inflate(layoutInflater)

        val options =
            FirebaseTranslatorOptions.Builder() // below line we are specifying our source language.
                .setSourceLanguage(FirebaseTranslateLanguage.EN) // in below line we are displaying our target language.
                .setTargetLanguage(FirebaseTranslateLanguage.DE) // after that we are building our options.
                .build()
        // below line is to get instance
        // for firebase natural language.
        // below line is to get instance
        // for firebase natural language.
//        englishSpanishTranslator!!.downloadModelIfNeeded()
        englishSpanishTranslator = FirebaseNaturalLanguage.getInstance().getTranslator(options)

//        var firebaseTranslator = FirebaseNaturalLanguage.getInstance()

        val button: Button = findViewById(R.id.learnTranslateButton)
        var edtText : EditText = findViewById(R.id.learnEditText);



        Log.d("XXX","end of on create")
        button.setOnClickListener {
            Log.d("XXX", "learnTranslateBUtton")
            var toTranslateString = edtText.text.toString()
            var toLog = "Word =" + toTranslateString
            Log.d("XXX", toLog)
            downloadModal(toTranslateString)
        }

//        learnBinding.learnAudio.setOnClickListener {
//            Log.d("XXX", "Audio")
//        }

    }

    private fun downloadModal(input: String) {
        // below line is use to download the modal which
        // we will require to translate in german language
        val conditions = FirebaseModelDownloadConditions.Builder().requireWifi().build()

        // below line is use to download our modal.
        englishSpanishTranslator!!.downloadModelIfNeeded(conditions)
            .addOnSuccessListener(OnSuccessListener<Void?> { // this method is called when modal is downloaded successfully.
                Toast.makeText(
                    this,
                    "Please wait language modal is being downloaded.",
                    Toast.LENGTH_SHORT
                ).show()

                // calling method to translate our entered text.
                translateLanguage(input)
            }).addOnFailureListener(OnFailureListener {
                Toast.makeText(
                    this,
                    "Fail to download modal",
                    Toast.LENGTH_SHORT
                ).show()
            })
    }

    private fun translateLanguage(input: String) {
        var translatedSentence : TextView = findViewById(R.id.translatedSentence);
        englishSpanishTranslator!!.translate(input)
            .addOnSuccessListener(OnSuccessListener<String?> { s -> translatedSentence.setText(s) })
            .addOnFailureListener(
                OnFailureListener {
                    Toast.makeText(
                        this,
                        "Fail to translate",
                        Toast.LENGTH_SHORT
                    ).show()
                })
    }


}