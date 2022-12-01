package com.mobilecomputing.englishtoromance

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.mobilecomputing.englishtoromance.databinding.ActivityLearnBinding
import com.mobilecomputing.englishtoromance.databinding.ActivityTestBinding

private lateinit var testBinding: ActivityTestBinding
class activity_test : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        testBinding = ActivityTestBinding.inflate(layoutInflater)
        if (this.supportActionBar != null) {
            this.supportActionBar!!.hide();
        }
        var exitButton : Button = findViewById(R.id.exitTest)

        exitButton.setOnClickListener {
            var data = Intent()
            setResult(RESULT_OK, data);
            finish()
        }
    }
}