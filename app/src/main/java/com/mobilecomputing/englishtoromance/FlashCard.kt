package com.mobilecomputing.englishtoromance

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class FlashCard : AppCompatActivity() {
    private var count = 0 // number of words searched through
    lateinit var front_anim:AnimatorSet
    lateinit var back_anim:AnimatorSet
    var isFront = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flash_card)

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


    }
}