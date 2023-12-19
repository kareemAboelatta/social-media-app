package com.example.socialmediaapp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.socialmediaapp.firebase.FirebaseService
import com.example.socialmediaapp.firebase.MyFirebase
import com.example.socialmediaapp.models.NotificationData
import com.example.socialmediaapp.models.PushNotification
import com.example.socialmediaapp.network.RetrofitBuilder
import com.google.firebase.iid.FirebaseInstanceIdReceiver
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class TestActivity : AppCompatActivity() {
    val TAG = "TestActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)


/*        button.setOnClickListener {
            val text = et_first.text.toString()
            if (title.isNotEmpty()) {
                translateThisIntoMyLanguage(text)

            }
        }*/
    }


    val languageIdentifier = LanguageIdentification.getClient()
    fun translateThisIntoMyLanguage(text: String) {
        //languageIdentifier
        var sourceLanguage: String = ""
        val myLanguage = Locale.getDefault().language
        languageIdentifier.identifyLanguage(text)
            .addOnSuccessListener { languageCode ->
                if (languageCode == "und") {
                    Log.i(TAG, "Can't identify language.")
                    Toast.makeText(this, "Can't identify language.", Toast.LENGTH_SHORT).show()
                } else {
                    Log.i(TAG, "Language: $languageCode")
                    sourceLanguage = TranslateLanguage.fromLanguageTag(languageCode).toString()
                    translate(text, sourceLanguage, myLanguage)
                }
            }
            .addOnFailureListener {
                // Model couldn’t be loaded or other internal error.
                // ...
            }


    }
    fun translate(text: String,sourceLanguage:String ,myLanguage:String):String{
        var targetLanguage=""

        // Create an sourceLanguage-myLanguage translator:
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(sourceLanguage)
            .setTargetLanguage(myLanguage)
            .build()

        val translator = Translation.getClient(options)
        var conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()

        translator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                // Model downloaded successfully. Okay to start translating.
                // (Set a flag, unhide the translation UI, etc.)
            }
            .addOnFailureListener { exception ->
                // Model couldn’t be downloaded or other internal error.
                // ...
            }


        //translate
        translator.translate(text)
            .addOnSuccessListener { translatedText ->
                // Translation successful.
                targetLanguage=translatedText
//                et_second.setText(translatedText + " was \n \"$sourceLanguage\" to \"$myLanguage\"")
            }
            .addOnFailureListener { exception ->
                // Error.
                // ...
            }
        return targetLanguage
    }


}




