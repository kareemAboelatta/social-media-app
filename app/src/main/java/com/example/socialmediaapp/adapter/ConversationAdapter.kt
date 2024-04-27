package com.example.socialmediaapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.RecyclerView
import com.example.socialmediaapp.databinding.ReceivedMsgLayoutBinding
import com.example.socialmediaapp.databinding.SentMsgLayoutBinding
import com.example.socialmediaapp.models.Message
import com.example.socialmediaapp.common.Utils
import com.google.mlkit.nl.languageid.LanguageIdentification

class ConversationAdapter(var messages: List<Message>, private val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            MESSAGE_TYPE_SENT -> SentViewHolder(SentMsgLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            else -> ReceivedViewHolder(ReceivedMsgLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        val sameAsPrevious = position > 0 && messages[position].sender == messages[position - 1].sender
        val atTheSameDay = Utils.checkConversationDate(messages, position)

        when (holder) {
            is SentViewHolder -> holder.bind(message, sameAsPrevious, atTheSameDay)
            is ReceivedViewHolder -> holder.bind(message, sameAsPrevious, atTheSameDay)
        }
    }

    override fun getItemCount() = messages.size

    override fun getItemViewType(position: Int): Int {
        return Utils.getMessageType(messages[position])
    }

    inner class SentViewHolder(private val binding: SentMsgLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message, sameAsPrevious: Boolean, atTheSameDay: Boolean) {
            with(binding) {
                val padding = if (!sameAsPrevious) 10 else 4
                val dpPadding = Utils.convertPxToDp(context, padding)
                root.updatePadding(top = dpPadding)

                sentConversationDate.apply {
                    text = if (!atTheSameDay) Utils.getChatTime(message.time) else ""
                    visibility = if (!atTheSameDay) View.VISIBLE else View.GONE
                }

                sentMsgText.text = message.body
                sentMsgTime.text = Utils.getMessageTime(message.time)
            }
        }
    }

    inner class ReceivedViewHolder(private val binding: ReceivedMsgLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message, sameAsPrevious: Boolean, atTheSameDay: Boolean) {
            with(binding) {
                val padding = if (!sameAsPrevious) 10 else 4
                val dpPadding = Utils.convertPxToDp(context, padding)
                root.updatePadding(top = dpPadding)

                receivedConversationDate.apply {
                    text = if (!atTheSameDay) Utils.getChatTime(message.time) else ""
                    visibility = if (!atTheSameDay) View.VISIBLE else View.GONE
                }

                receivedMsgText.text = message.body
                receivedMsgTime.text = Utils.getMessageTime(message.time)
            }
        }
    }

    companion object {
        private const val MESSAGE_TYPE_SENT = 1
        private const val MESSAGE_TYPE_RECEIVED = 2
    }


    private val languageIdentifier = LanguageIdentification.getClient()
/*

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

*/

}