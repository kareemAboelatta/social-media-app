package com.example.common.ui


import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.RotateAnimation
import android.view.animation.ScaleAnimation
import android.widget.ImageView
import android.widget.TextView
import com.example.common.R


class ProgressDialogUtil {

    private var progressDialog: AlertDialog? = null
    private var progressIcon: ImageView? = null

    fun showProgressDialog(context: Context, message: String = "Loading..."){
        val builder = AlertDialog.Builder(context, R.style.CustomAlertDialog)


        val dialogView: View =
            LayoutInflater.from(context).inflate(R.layout.progress_dialog, null)


        progressIcon = dialogView.findViewById(R.id.progressIcon)

        val progressMessage: TextView = dialogView.findViewById(R.id.progressMessage)
        progressMessage.text = message


        builder.setView(dialogView)


        builder.setView(dialogView)
        builder.setCancelable(false)

        // Create the scale-in animation
        val scaleInAnimation = ScaleAnimation(
            0.5f, 1.1f,  // Start and end scale X
            0.5f, 1.1f,  // Start and end scale Y
            Animation.RELATIVE_TO_SELF, 0.5f,  // Pivot point X
            Animation.RELATIVE_TO_SELF, 0.5f   // Pivot point Y
        )
        scaleInAnimation.duration = 800 // Duration in milliseconds
        scaleInAnimation.repeatCount = Animation.INFINITE

        // Create the scale-out animation
        val scaleOutAnimation = ScaleAnimation(
            1.1f, 0.5f,  // Start and end scale X
            1.1f, 0.5f,  // Start and end scale Y
            Animation.RELATIVE_TO_SELF, 0.5f,  // Pivot point X
            Animation.RELATIVE_TO_SELF, 0.5f   // Pivot point Y
        )
        scaleOutAnimation.duration = 900 // Duration in milliseconds
        scaleOutAnimation.repeatCount = Animation.INFINITE


        // Create an AnimationSet to combine the two animations
        val animationSet = AnimationSet(true)
        animationSet.addAnimation(scaleInAnimation)
        animationSet.addAnimation(scaleOutAnimation)

        // Apply the animation to the icon
        progressIcon?.animation = animationSet

        progressDialog = builder.create()
        progressDialog?.show()
    }


    fun hideProgressDialog() {
        // Stop the animation
        progressIcon?.animation?.cancel()
        progressDialog?.dismiss()


    }


}


