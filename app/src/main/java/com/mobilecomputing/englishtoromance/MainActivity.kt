package com.mobilecomputing.englishtoromance

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.view.MenuProvider
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.android.material.snackbar.Snackbar
import com.mobilecomputing.englishtoromance.databinding.ActivityMainBinding
import com.mobilecomputing.englishtoromance.databinding.ContentMainBinding



class MainActivity : AppCompatActivity() {
    private lateinit var contentMainBinding: ContentMainBinding

    private var resultLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                Log.d(javaClass.simpleName, "result ok")
            } else {
                Log.w(javaClass.simpleName, "Bad activity return code ${result.resultCode}")
            }
        }

    private val viewModel: MainViewModel by viewModels()

    private val signInLauncher =
        registerForActivityResult(FirebaseAuthUIActivityResultContract()) {
            viewModel.updateUser()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)
        contentMainBinding = activityMainBinding.contentMain

        if (this.getSupportActionBar() != null) {
            this.getSupportActionBar()!!.hide();
        }

        contentMainBinding.clickitLaunch.setOnClickListener {
            intent = Intent(this, activity_click_it::class.java)
            Log.d("XXX", "Launched activity")
            resultLauncher.launch(intent)
        }

        contentMainBinding.flashCardLaunch.setOnClickListener {
            intent = Intent(this, FlashCard::class.java)
            Log.d("XXX", "Launched activity")
            resultLauncher.launch(intent)
        }

        contentMainBinding.learnLaunch.setOnClickListener {
            intent = Intent(this, Learn::class.java)
            Log.d("XXX", "Launched activity")
            resultLauncher.launch(intent)
        }

        contentMainBinding.testLaunch.setOnClickListener {
            intent = Intent(this, activity_test::class.java)
            Log.d("XXX", "Launched activity")
            resultLauncher.launch(intent)
        }

        AuthInit(viewModel, signInLauncher)



    }
}