package com.example.socialmediaapp.di

import android.app.ProgressDialog
import android.content.Context
import android.speech.tts.TextToSpeech
import android.widget.ProgressBar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.socialmediaapp.R
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.*
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideTestString1() = "This is a string we will inject"


    @Singleton
    @Provides
    fun provideContext(@ApplicationContext context: Context) = context

    @Singleton
    @Provides
    fun provideSimpleExoPlayer(@ApplicationContext context: Context) = SimpleExoPlayer.Builder(context).build()


    @Singleton
    @Provides
    fun provideGlideInstance(
        @ApplicationContext context: Context
    ) = Glide.with(context).setDefaultRequestOptions(
        RequestOptions()
            .placeholder(R.color.blackColor )
            .error(R.drawable.ic_image_default)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
    )


    @Singleton
    @Provides
    fun provideTextToSpeech(@ApplicationContext context: Context):TextToSpeech =
        TextToSpeech(context){}

}