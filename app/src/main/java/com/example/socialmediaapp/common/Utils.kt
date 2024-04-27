package com.example.socialmediaapp.common

import android.content.Context
import com.example.socialmediaapp.firebase.MyFirebase.mAuth
import com.example.socialmediaapp.models.Message
import java.text.SimpleDateFormat
import java.util.*

class Utils {




    companion object {
        fun getMessageType(message: Message): Int {
            val uid = mAuth.currentUser?.uid.toString()
            return if (message.sender == uid) 1 else 2
        }

        fun getMessageTime(timeInMillis: String): String {
            val timeFormat = SimpleDateFormat("hh:mm a")
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = timeInMillis.toLong()
            return clearLeadingZeros(timeFormat.format(calendar.time))
        }

        fun getChatID(senderID: String, receiverID: String) =
            if (senderID < receiverID) senderID + receiverID else receiverID + senderID

        fun getChatTime(time: String): String {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy")
            val timeFormat = SimpleDateFormat("hh:mm a")
            val calendar1 = Calendar.getInstance()
            val calendar2 = Calendar.getInstance()

            calendar1.timeInMillis = System.currentTimeMillis()
            calendar2.timeInMillis = time.toLong()

            if (calendar1.get(Calendar.YEAR) != calendar2.get(Calendar.YEAR))    // in different years
                return dateFormat.format(calendar2.time)

            if (calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH))  // in same month
            {
                val diff =
                    calendar1.get(Calendar.DAY_OF_MONTH) - calendar2.get(Calendar.DAY_OF_MONTH)
                if (diff < 7) {
                    if (diff == 0) return clearLeadingZeros(timeFormat.format(calendar2.time)     )   // in the same day
                    if (diff == 1) return "Yesterday"
                    return when (calendar2.get(Calendar.DAY_OF_WEEK)) {
                        Calendar.SATURDAY -> "Saturday"
                        Calendar.SUNDAY -> "Sunday"
                        Calendar.MONDAY -> "Monday"
                        Calendar.TUESDAY -> "Tuesday"
                        Calendar.WEDNESDAY -> "Wednesday"
                        Calendar.THURSDAY -> "Thursday"
                        else -> "Friday"
                    }
                }
            }
            return dateFormat.format(calendar2.time)
        }
        private fun clearLeadingZeros(time:String):String{
            return if(time[0]=='0') time.drop(1) else time
        }
        fun convertPxToDp(context: Context, px: Int): Int {
            val scale = context.resources.displayMetrics.density
            return (px * scale + 0.5f).toInt()
        }

        fun checkConversationDate(messages: List<Message>, position: Int): Boolean {
            if (position > 0) {
                val calendar1 = Calendar.getInstance()
                val calendar2 = Calendar.getInstance()
                calendar1.timeInMillis = messages[position].time.toLong()
                calendar2.timeInMillis = messages[position - 1].time.toLong()
                val diff = calendar1.timeInMillis - calendar2.timeInMillis
                val days = diff / (1000 * 60 * 60 * 24)

                return if (days == 0L) {
                    calendar1[Calendar.DAY_OF_MONTH] == calendar2[Calendar.DAY_OF_MONTH]
                } else false
            }
            return false
        }
    }
}