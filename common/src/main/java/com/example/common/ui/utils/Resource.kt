package com.example.common.ui.utils

data class Resource<out T>(val status: Status, val data:T?, val message:String?){
    companion object{
        fun <T> success(data:T?): Resource<T> {
            return Resource(Status.SUCCESS,data,null)
        }
        fun <T> loading(data:T?): Resource<T> {
            return Resource(Status.LOADING,data,null)
        }
        fun <T> error(msg:String,data:T?): Resource<T> {
            return Resource(Status.ERROR,data,msg)
        }
    }
}




sealed class UIState<out T> {
    data class Success<T>(val data: T) : UIState<T>()
    data class Error(val error: String) : UIState<Nothing>()
    data object Loading : UIState<Nothing>()
    data object Empty : UIState<Nothing>()
}


