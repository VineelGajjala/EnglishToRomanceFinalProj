package com.mobilecomputing.englishtoromance

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions
import com.mobilecomputing.englishtoromance.databinding.ActivityClickItBinding
import java.util.*
import com.mobilecomputing.englishtoromance.api.WordsApi
import com.mobilecomputing.englishtoromance.api.WordsRepository
import kotlin.random.Random

class activity_click_it : AppCompatActivity() {

    var translator: FirebaseTranslator? = null
    private lateinit var clickItBinding: ActivityClickItBinding
    private var percentage : Double = 0.0
    private var fromLanguage = 0
    private var toLanguage = 1
    private var totalWords = 10
    private var wordsLeft = totalWords
    private val neutralBgColor = R.color.Button_Accent
    private val warnColor = Color.BLACK
    private val wordsApi = WordsApi.create()
    private val wordsRepo = WordsRepository(wordsApi)
    private val allWords = wordsRepo.getWords()
    private var testWord: TextView = findViewById(R.id.test_word)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_click_it)
        clickItBinding = ActivityClickItBinding.inflate(layoutInflater)

        if (this.supportActionBar != null) {
            this.supportActionBar!!.hide();
        }

        val options =
            FirebaseTranslatorOptions.Builder()
                .setSourceLanguage(FirebaseTranslateLanguage.EN)
                .setTargetLanguage(FirebaseTranslateLanguage.ES)
                .build()

        translator = FirebaseNaturalLanguage.getInstance().getTranslator(options)

        var exitButton : Button = findViewById(R.id.exitClickIt)
        var switchButton : ImageButton = findViewById(R.id.switchLanguage)

        exitButton.setOnClickListener {
            var data = Intent()
            data.putExtra("game", "Click-It")
            data.putExtra("percentage-correct", percentage)
            setResult(RESULT_OK, data);
            finish()
        }

        switchButton.setOnClickListener {
            if (switchButton.background.equals(android.R.drawable.arrow_up_float)){
                switchButton.setBackgroundResource(android.R.drawable.arrow_down_float)
                fromLanguage = 1
                toLanguage = 0
                setTranslator()
            } else {
                switchButton.setBackgroundResource(android.R.drawable.arrow_up_float)
                fromLanguage = 0
                toLanguage = 1
                setTranslator()
            }
        }

        // play game
        while (wordsLeft > 0){
            playRound {
                percentage = (1 - (wordsLeft * .1)) * 10
            }
        }
    }

    private fun downloadModal(input: String, button: Button?) {
        // below line is use to download the modal which
        // we will require to translate in german language
        val conditions = FirebaseModelDownloadConditions.Builder().requireWifi().build()

        // below line is use to download our modal.
        translator!!.downloadModelIfNeeded(conditions)
            .addOnSuccessListener(OnSuccessListener<Void?> { // this method is called when modal is downloaded successfully.
                Toast.makeText(
                    this,
                    "Please wait language modal is being downloaded.",
                    Toast.LENGTH_SHORT
                ).show()

                // calling method to translate our entered text.
                if (button == null){
                    translateLanguage(input)
                } else {
                    translateLanguageForButton(input, button)
                }
            }).addOnFailureListener(OnFailureListener {
                Toast.makeText(
                    this,
                    "Fail to download modal",
                    Toast.LENGTH_SHORT
                ).show()
            })
    }

    private fun translateLanguageForButton(input: String, button: Button) {
        var translatedWord = button;
        translator!!.translate(input)
            .addOnSuccessListener(OnSuccessListener<String?> { s -> translatedWord.text = s })
            .addOnFailureListener(
                OnFailureListener {
                    Toast.makeText(
                        this,
                        "Fail to translate",
                        Toast.LENGTH_SHORT
                    ).show()
                })
    }

    private fun translateLanguage(input: String) {
        translator!!.translate(input)
            .addOnSuccessListener(OnSuccessListener<String?> { s -> testWord.text = s })
            .addOnFailureListener(
                OnFailureListener {
                    Toast.makeText(
                        this,
                        "Fail to translate",
                        Toast.LENGTH_SHORT
                    ).show()
                })
    }

    private fun setTranslator() {
        val options =
            FirebaseTranslatorOptions.Builder()
        if (fromLanguage == 0) {
            options.setSourceLanguage(FirebaseTranslateLanguage.EN)
        } else if (fromLanguage == 1) {
            options.setSourceLanguage(FirebaseTranslateLanguage.ES)
        }

        if (toLanguage == 0) {
            options.setTargetLanguage(FirebaseTranslateLanguage.EN)
        } else if (toLanguage == 1) {
            options.setTargetLanguage(FirebaseTranslateLanguage.ES)
        }

        translator = FirebaseNaturalLanguage.getInstance().getTranslator(options.build())
    }

    private fun outOfOrderPick(view: View) {
        val colorToWarn : Animator = ValueAnimator
            .ofObject(ArgbEvaluator(), neutralBgColor, warnColor)
            .apply{duration = 200} // milliseconds
            .apply{addUpdateListener { animator -> view.setBackgroundColor(animator.animatedValue as Int) }}
        val colorFromWarn = ValueAnimator
            .ofObject(ArgbEvaluator(), warnColor, neutralBgColor)
            .apply{duration = 350}
            .apply{addUpdateListener { animator -> view.setBackgroundColor(animator.animatedValue as Int) }}
        val animatorSet = AnimatorSet()
        animatorSet.playSequentially(
            colorToWarn,
            colorFromWarn
        )
        animatorSet.start()
    }

    private fun playRound (roundDone: () -> Unit){
        // get the list of words to be used
        var gameWords = mutableListOf<String>()
        var testWord : TextView = findViewById(R.id.test_word);
        var answer1 : Button = findViewById(R.id.word_1)
        var answer2 : Button = findViewById(R.id.word_2)
        var answer3 : Button = findViewById(R.id.word_3)
        var answer4 : Button = findViewById(R.id.word_4)
        var correctButton = 0


        gameWords.clear()
        for (i in 0..3 ){
            gameWords.add(allWords[Random.nextInt(allWords.size) - 1])
        }

        // gather word to test and check to see if it should be translated based on switch element
        val wordToTest = gameWords[Random.nextInt(gameWords.size) - 1]

        // show test word
        if (fromLanguage == 1){
            downloadModal(wordToTest, null)
        } else {
            testWord.text = wordToTest
        }

        // set the words for the buttons
        for (i in 4 downTo 1){
            val word = gameWords[Random.nextInt(i - 1)]
            if (wordToTest == word){correctButton = i}
            when (i) {
                1 -> {
                    if (fromLanguage == 0) {
                        downloadModal(word, answer1)
                    } else {
                        answer1.text = word
                    }
                }

                2 -> {
                    if (fromLanguage == 0) {
                        downloadModal(word, answer1)
                    } else {
                        answer1.text = word
                    }
                }

                3 -> {
                    if (fromLanguage == 0) {
                        downloadModal(word, answer1)
                    } else {
                        answer1.text = word
                    }
                }

                4 -> {
                    if (fromLanguage == 0) {
                        downloadModal(word, answer1)
                    } else {
                        answer1.text = word
                    }
                }
            }
        }

        // set click listeners
        answer1.setOnClickListener{
            if (correctButton == 1){
                // have something that notifies to go to the next round
                wordsLeft--
                roundDone()
            } else {
                outOfOrderPick(answer1)
            }
        }
        answer2.setOnClickListener{
            if (correctButton == 2){
                // have something that notifies to go to the next round
                wordsLeft--
                roundDone()
            } else {
                outOfOrderPick(answer2)
            }
        }
        answer3.setOnClickListener{
            if (correctButton == 3){
                // have something that notifies to go to the next round
                wordsLeft--
                roundDone()
            } else {
                outOfOrderPick(answer2)
            }
        }
        answer4.setOnClickListener{
            if (correctButton == 4){
                // have something that notifies to go to the next round
                wordsLeft--
                roundDone()
            } else {
                outOfOrderPick(answer4)
            }
        }
    }
}