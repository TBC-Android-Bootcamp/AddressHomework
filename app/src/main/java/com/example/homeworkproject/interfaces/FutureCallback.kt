package com.example.homeworkproject.interfaces

interface FutureCallback<T> {
    fun  onSuccess(result:String)
    fun doneResults(map:MutableMap<String,String>)
}
