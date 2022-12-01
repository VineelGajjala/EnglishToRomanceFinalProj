package com.mobilecomputing.englishtoromance

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText

class makeNewFlashcard : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_make_new_flashcard)

        if (this.getSupportActionBar() != null) {
            this.getSupportActionBar()!!.hide();
        }

        var englishWord : EditText = findViewById(R.id.makeFlashEnglish)
        var spanishWord : EditText = findViewById(R.id.makeFlashSpanish)
        var exitButton : Button = findViewById(R.id.makeFlashCardMake)

        exitButton.setOnClickListener {
            var data = Intent()
            data.putExtra("english", englishWord.text.toString())
            data.putExtra("spanish", spanishWord.text.toString())
            Log.d("XXX", englishWord.text.toString())
            setResult(RESULT_OK, data);
            finish()
        }

    }
}