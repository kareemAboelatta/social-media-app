package com.example.socialmediaapp.models


data class Message(
    var body:String="",
    var time:String="",
    var sender:String="",
    var receiver:String="",
    var languageCode:String="und",
    var translatedContent:String=""
)
