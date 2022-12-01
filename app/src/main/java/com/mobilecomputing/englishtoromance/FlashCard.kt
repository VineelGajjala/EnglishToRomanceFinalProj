package com.mobilecomputing.englishtoromance

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.ColorFilter
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationSet
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import edu.utap.firebaseauth.MainViewModel


class FlashCard : AppCompatActivity() {

    private var resultLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                Log.d(javaClass.simpleName, "result ok")
                val data = result.data
                val english = data!!.getStringExtra("english")
                val spanish = data!!.getStringExtra("spanish")
                if (english != "" && spanish != "") {
                    viewModel.addToAll(english!!, spanish!!)
                }
            } else {
                Log.w(javaClass.simpleName, "Bad activity return code ${result.resultCode}")
            }
        }


    private var count = 0 // number of words searched through
    lateinit var front_anim:AnimatorSet
    lateinit var back_anim:AnimatorSet

    var isFront = true

    private lateinit var colorFilter : ColorFilter

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flash_card)

        viewModel.setInitialValues()
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

        nextWordButton.setOnClickListener {
            var x:String = "X:" + cardFront.x.toString()
            Log.d("XXX", x)

            cardFront.animate()
                .setDuration(500)
                .translationX(-1300f)
                .withEndAction(Runnable {

                    viewModel.increaseIndex()

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

        }

        previousWordButton.setOnClickListener {

            var x:String = "X:" + cardFront.x.toString()
            Log.d("XXX", x)

            cardFront.animate()
                .setDuration(500)
                .translationX(1300f)
                .withEndAction(Runnable {

                    viewModel.decreaseIndex()

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

        }

        // Star functionality
        var starButton : ImageView = findViewById(R.id.starFlash)
        starButton.setColorFilter(Color.argb(255, 50, 50, 50))
        colorFilter = starButton.colorFilter

        starButton.setOnClickListener {
            if (starButton.colorFilter == colorFilter) {
                starButton.setColorFilter(Color.argb(255, 255, 214, 0));
                viewModel.addToFavorites()
            } else {
                starButton.setColorFilter(colorFilter)
                viewModel.removeFavorite()
            }
//            starButton.setColorFilter(Color.argb(255, 255, 214, 0));
            Log.d("XXX", starButton.colorFilter.toString())
//            print("XXX:" + starButton.colorFilter.toString())
        }

        viewModel.observeCurrentFlashCard().observe(this) {
            cardBack.text = it.englishWord
            cardFront.text = it.spanishWord

            if (it in viewModel.getFavorited()) {
                starButton.setColorFilter(Color.argb(255, 255, 214, 0));
            } else {
                starButton.setColorFilter(colorFilter)
            }
        }

        var makeFlash : Button = findViewById(R.id.addFlash)

        makeFlash.setOnClickListener {
            intent = Intent(this, makeNewFlashcard::class.java)
            Log.d("XXX", "Launched activity")
            resultLauncher.launch(intent)
        }




    }
}