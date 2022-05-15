package com.example.socialmediaapp.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.RecyclerView
import com.example.socialmediaapp.R
import com.example.socialmediaapp.models.Message
import com.example.socialmediaapp.utils.Utils
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.android.synthetic.main.activity_test.*
import kotlinx.android.synthetic.main.received_msg_layout.view.*
import kotlinx.android.synthetic.main.sent_msg_layout.view.*
import java.util.*

class ConversationAdapter( var messages: List<Message> , var context : Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            1 -> ViewHolder1(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.sent_msg_layout, parent, false)
            )
            else -> ViewHolder2(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.received_msg_layout, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var sameAsPrevious=false
        if(position>0) sameAsPrevious=messages[position].sender== messages[position-1].sender
        val atTheSameDay=Utils.checkConversationDate(messages,position)
        when (holder.itemViewType) {
            1 -> {
                (holder as ViewHolder1).bind(messages[position],sameAsPrevious,atTheSameDay)
            }
            else -> {

                (holder as ViewHolder2).bind(messages[position],sameAsPrevious,atTheSameDay)
                translateThisIntoMyLanguage(messages[position].body, holder.itemView )

            }
        }
    }

    override fun getItemCount()=messages.size

    override fun getItemViewType(position: Int): Int {
        return Utils.getMessageType(messages[position])
    }

    class ViewHolder1(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(message: Message,sameAsPrevious:Boolean,atTheSameDay:Boolean) {

            if(!sameAsPrevious){     // large space between messages
                val dp=Utils.convertPxToDp(itemView.context,10)
                itemView.updatePadding(top = dp)
            }else {                  // set as normal
                val dp=Utils.convertPxToDp(itemView.context,4)
                itemView.updatePadding(top = dp)
            }

            if(!atTheSameDay){
                itemView.sent_conversation_date.text=Utils.getChatTime(message.time)
                itemView.sent_conversation_date.visibility= VISIBLE
            }else itemView.sent_conversation_date.visibility= GONE

            itemView.sent_msg_text.text=message.body

            itemView.sent_msg_time.text=Utils.getMessageTime(message.time)
        }
    }

    class ViewHolder2(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(message: Message,sameAsPrevious:Boolean,atTheSameDay:Boolean) {

            if(!sameAsPrevious){   // large space between messages
                val dp=Utils.convertPxToDp(itemView.context,10)
                itemView.updatePadding(top = dp)
            } else {                 // set as normal
                val dp=Utils.convertPxToDp(itemView.context,4)
                itemView.updatePadding(top = dp)
            }

            if(!atTheSameDay){
                itemView.received_conversation_date.text=Utils.getChatTime(message.time)
                itemView.received_conversation_date.visibility= VISIBLE
            }else itemView.received_conversation_date.visibility= GONE

            itemView.received_msg_text.text=message.body


            itemView.received_msg_time.text=Utils.getMessageTime(message.time)
        }
    }



    val languageIdentifier = LanguageIdentification.getClient()

    private fun translateThisIntoMyLanguage(text: String,itemView : View) :String {
        var targetLanguage="i can't wait"
        //languageIdentifier
        var sourceLanguage: String = ""
        val myLanguage = Locale.getDefault().language
        languageIdentifier.identifyLanguage(text)
            .addOnSuccessListener { languageCode ->
                if (languageCode == "und") {
                    Toast.makeText(context, "Can't identify language.", Toast.LENGTH_SHORT).show()
                } else {
                    sourceLanguage = TranslateLanguage.fromLanguageTag(languageCode).toString()
                    if (sourceLanguage != myLanguage) {
                        targetLanguage = translate(text, sourceLanguage, myLanguage, itemView)
                    }
                }
            }
            .addOnFailureListener {
                // Model couldn’t be loaded or other internal error.
                // ...
            }

        return targetLanguage
    }
    fun translate(text: String,sourceLanguage:String ,myLanguage:String,itemView : View):String{
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
                //translate
                translator.translate(text)
                    .addOnSuccessListener { translatedText ->
                        // Translation successful.
                        targetLanguage=translatedText
                        itemView.received_msg_text.text=translatedText
                        itemView.received_msg_lang.visibility= VISIBLE
                        itemView.received_msg_lang.text="translated  from $sourceLanguage see original"
                        itemView.received_msg_lang.setOnClickListener {
                            itemView.received_msg_text.text=text
                            itemView.received_msg_lang.visibility= GONE

                        }
                    }
                    .addOnFailureListener { exception ->
                        // Error.
                        // ...
                    }

            }
            .addOnFailureListener { exception ->
                // Model couldn’t be downloaded or other internal error.
                // ...
            }



        return targetLanguage
    }


}