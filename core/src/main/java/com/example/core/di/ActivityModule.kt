package com.example.core.di

import android.app.Activity
import com.example.core.ui.ProgressDialogUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
object ActivityModule {

    @Provides
    @ActivityScoped
    fun provideProgressUtil(activity: Activity): ProgressDialogUtil{
        return ProgressDialogUtil(activity)
    }

}