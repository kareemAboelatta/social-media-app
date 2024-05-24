package com.example.common

import javax.inject.Qualifier


@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Dispatcher(val appDispatcher: AppDispatcher)

enum class AppDispatcher {
    Default,
    IO,
}