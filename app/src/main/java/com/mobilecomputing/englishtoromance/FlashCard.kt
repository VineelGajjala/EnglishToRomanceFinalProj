package com.mobilecomputing.englishtoromance

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Color
import android.graphics.ColorFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationSet
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import java.util.*


class FlashCard : AppCompatActivity() {
    private var count = 0 // number of words searched through
    lateinit var front_anim:AnimatorSet
    lateinit var back_anim:AnimatorSet

    private var englishWords = mutableListOf<String>()
    private var spanishWords = mutableListOf<String>()
    private var currentIndex = 0

    var isFront = true

    private lateinit var colorFilter : ColorFilter

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flash_card)

        viewModel.fetchflashCards()

        //exit intent
        var exitButton : Button = findViewById(R.id.exitFlash)

        exitButton.setOnClickListener {
            var data = Intent()
            data.putExtra("game", "FlashCards")
            data.putExtra("flashCardsCounted", count)
            setResult(RESULT_OK, data);
            finish()
        }

        if (this.getSupportActionBar() != null) {
            this.getSupportActionBar()!!.hide();
        }

        //this part is for flipping a card from word to translated
        var flipButton : Button = findViewById(R.id.flipFlash)
        var cardFront : TextView = findViewById(R.id.frontCard)
        var cardBack : TextView = findViewById(R.id.backCard)

        val scale:Float = this.resources.displayMetrics.density
        cardFront.cameraDistance = 8000 * scale
        cardBack.cameraDistance = 8000 * scale

        front_anim = AnimatorInflater.loadAnimator(this, R.animator.front_animation) as AnimatorSet
        back_anim = AnimatorInflater.loadAnimator(this, R.animator.back_animation) as AnimatorSet

        flipButton.setOnClickListener {
            if (isFront) {
                front_anim.setTarget(cardFront)
                back_anim.setTarget(cardBack)
                front_anim.start()
                back_anim.start()
                isFront = false
            } else {
                front_anim.setTarget(cardBack)
                back_anim.setTarget(cardFront)
                back_anim.start()
                front_anim.start()
                isFront = true

            }
        }

        //nextWord
        //previous Word
        //Transition between different words

        var nextWordButton : Button = findViewById(R.id.nextFlash)
        var previousWordButton : Button = findViewById(R.id.previousFlash)
        var starButton : ImageView = findViewById(R.id.starFlash)

        nextWordButton.setOnClickListener {
            var x:String = "X:" + cardFront.x.toString()
            Log.d("XXX", x)

            cardFront.animate()
                .setDuration(500)
                .translationX(-1300f)
                .withEndAction(Runnable {

                    currentIndex += 1
                    currentIndex %= spanishWords.size
                    cardBack.text = englishWords[currentIndex]
                    cardFront.text = spanishWords[currentIndex]

                    cardBack.x = 1300f
                    cardFront.x = 1300f
                    // Do something.
                    ObjectAnimator.ofFloat(cardFront, "translationX", 0f).apply {
                        duration = 500
                        start()
                    }

                    ObjectAnimator.ofFloat(cardBack, "translationX", 0f).apply {
                        duration = 500
                        start()
                    }

                    x = "X:" + cardFront.x.toString()
                    Log.d("XXX", x)

                })
                .start()

            cardBack.animate()
                .setDuration(500)
                .translationX(-1300f)
                .start()

            starButton.setColorFilter(Color.argb(255, 255, 214, 0));
        }

        previousWordButton.setOnClickListener {

            var x:String = "X:" + cardFront.x.toString()
            Log.d("XXX", x)

            cardFront.animate()
                .setDuration(500)
                .translationX(1300f)
                .withEndAction(Runnable {

                    currentIndex -= 1
                    if (currentIndex < 0) { currentIndex = spanishWords.size - 1}
                    cardBack.text = englishWords[currentIndex]
                    cardFront.text = spanishWords[currentIndex]

                    cardBack.x = -1300f
                    cardFront.x = -1300f
                    // Do something.
                    ObjectAnimator.ofFloat(cardFront, "translationX", 0f).apply {
                        duration = 500
                        start()
                    }

                    ObjectAnimator.ofFloat(cardBack, "translationX", 0f).apply {
                        duration = 500
                        start()
                    }

                    x = "X:" + cardFront.x.toString()
                    Log.d("XXX", x)

                })
                .start()

            cardBack.animate()
                .setDuration(500)
                .translationX(1300f)
                .start()

            starButton.setColorFilter(Color.argb(255, 255, 214, 0));

        }

        // Star functionality

        starButton.setColorFilter(Color.argb(255, 50, 50, 50))
        colorFilter = starButton.colorFilter

        starButton.setOnClickListener {

            if (starButton.colorFilter == colorFilter) {
                starButton.setColorFilter(Color.argb(255, 255, 214, 0));
                var id = UUID.randomUUID()
                viewModel.createFlashCardMeta(englishWords[currentIndex], spanishWords[currentIndex],
                id.toString())
            } else {
                starButton.setColorFilter(colorFilter)
                viewModel.removeFlashCard(currentIndex)
            }
//            starButton.setColorFilter(Color.argb(255, 255, 214, 0));
            Log.d("XXX", starButton.colorFilter.toString())
//            print("XXX:" + starButton.colorFilter.toString())

        }
        viewModel.observeflashCards().observe(this) {
            Log.d("XXX", "here?")
            englishWords = mutableListOf<String>()
            spanishWords = mutableListOf<String>()

            for (flashcard in it) {
                englishWords.add(flashcard.englishWord)
                spanishWords.add(flashcard.spanishWord)
            }
        }

    }
}