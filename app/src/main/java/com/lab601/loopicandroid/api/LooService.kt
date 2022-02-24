package com.lab601.loopicandroid.api

import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path


interface LooService {

    @GET("story/scene/all")
    fun getAllSceneList(): Single<List<List<String>>>

    @GET("story/list")
    fun getStoryList(): Single<List<String>>

    @GET("scene/list/{sceneId}")
    fun getSceneList(@Path("sceneId") sceneId:Int):Single<List<String>>

    @GET("text/{storyId}")
    fun getStoryText(@Path("storyId") storyId:Int):Single<List<String>>

}