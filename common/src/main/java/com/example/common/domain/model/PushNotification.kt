package com.example.common.domain.model

data class PushNotification(
    val data: NotificationData,
    val to: String
)