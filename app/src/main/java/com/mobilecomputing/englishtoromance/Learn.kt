package com.mobilecomputing.englishtoromance

import android.R.attr.y
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions
import com.mobilecomputing.englishtoromance.databinding.ActivityLearnBinding
import java.util.*


class Learn : AppCompatActivity() {

    var englishSpanishTranslator: FirebaseTranslator? = null
    private lateinit var learnBinding: ActivityLearnBinding
    private var tts: TextToSpeech? = null
    private var count : Int = 0
    private var fromLanguage = 0
    private var toLanguage = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learn)
        learnBinding = ActivityLearnBinding.inflate(layoutInflater)

        if (this.getSupportActionBar() != null) {
            this.getSupportActionBar()!!.hide();
        }

        tts = TextToSpeech(
            this
        ) { status ->
            if (status != TextToSpeech.ERROR) {
                tts!!.setLanguage(Locale.US)
            }
        }

        val options =
            FirebaseTranslatorOptions.Builder()
                .setSourceLanguage(FirebaseTranslateLanguage.EN)
                .setTargetLanguage(FirebaseTranslateLanguage.ES)
                .build()

        englishSpanishTranslator = FirebaseNaturalLanguage.getInstance().getTranslator(options)


        val button: Button = findViewById(R.id.learnTranslateButton)
        var edtText : EditText = findViewById(R.id.learnEditText)
        var ttsButton : Button = findViewById(R.id.learnAudio)
        var exitButton : Button = findViewById(R.id.exitLearn)
        var speechToLearn : ImageView = findViewById(R.id.speechToTextLearn)
        var fromLanguageSpinner : Spinner = findViewById(R.id.fromLanguageLearn)
        var toLanguageSpinner : Spinner = findViewById(R.id.toLanguageLearn)

        var fromLanguages = arrayOf("From", "English", "Spanish")
        var toLanguages = arrayOf("To", "English", "Spanish")

        val fromLanguageAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, fromLanguages)
        fromLanguageSpinner.adapter = fromLanguageAdapter
        val toLanguageAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, toLanguages)
        toLanguageSpinner.adapter = toLanguageAdapter

        fromLanguageSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(arg0: AdapterView<*>?, arg1: View?, arg2: Int, arg3: Long) {
                val item: Int = fromLanguageSpinner.getSelectedItemPosition()
                val y = fromLanguages.get(item)
            }

            override fun onNothingSelected(arg0: AdapterView<*>?) {}
        })

        toLanguageSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(arg0: AdapterView<*>?, arg1: View?, arg2: Int, arg3: Long) {
                val item: Int = toLanguageSpinner.getSelectedItemPosition()
                val y = fromLanguages.get(item)
            }

            override fun onNothingSelected(arg0: AdapterView<*>?) {}
        })

        speechToLearn.setOnClickListener {
            var intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to Convert into Text")
            try {
                startActivityForResult(intent, 0)
            } catch (e : Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Something went wrong, Try again",
                    Toast.LENGTH_SHORT).show()
            }
        }

        exitButton.setOnClickListener {
            var data = Intent()
            data.putExtra("game", "Learn")
            data.putExtra("translations", count)
            setResult(RESULT_OK, data);
            finish()
        }


        Log.d("XXX","end of on create")
        button.setOnClickListener {
            Log.d("XXX", "learnTranslateBUtton")
            var toTranslateString = edtText.text.toString()
            var toLog = "Word =" + toTranslateString
            Log.d("XXX", toLog)
            count += 1
            downloadModal(toTranslateString)
        }

        ttsButton.setOnClickListener {
            Log.d("XXX", "Audio")
            var toTranslateString = edtText.text.toString()
            var toLog = "Word =" + toTranslateString
            Log.d("XXX", toLog)
            downloadModal(toTranslateString)
            var translatedSentence : TextView = findViewById(R.id.translatedSentence);
            var text = translatedSentence.text.toString()
            tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null,"")
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var edtText : EditText = findViewById(R.id.learnEditText)
        if (requestCode == 0) {
            if (resultCode == RESULT_OK && data != null) {
                var result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                edtText.setText(result!!.get(0), TextView.BufferType.EDITABLE)
                count += 1
                downloadModal(result!!.get(0))
            }
        }
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

    private fun fromSpinnerSelect(s : String) {

    }

    private fun toSpinnerSelect(s: String) {
//        if (s == "English") {
//            tts!!.language = Locale.US
//        } else if (s == "Spanish") {
//            val locSpanish = Locale("spa", "MEX")
//            tts!!.language = locSpanish
//        } else {
//            tts!!.language = null
//        }
    }


}