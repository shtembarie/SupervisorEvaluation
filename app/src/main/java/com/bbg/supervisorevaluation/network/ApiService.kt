package com.bbg.supervisorevaluation.network

import com.bbg.supervisorevaluation.data.SupervisorModel
import com.bbg.supervisorevaluation.data.SurveyResultModel
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*


interface ApiService {

    @GET("Survey/{surveyId}")
    fun getSurvey(@Path("surveyId") surveyId: Int): Call<JsonObject>

    @GET("Supervisor")
    fun getSupervisors(): Call<List<SupervisorModel>>

    @POST("SurveyResult")
    fun postSurveyResult(@Body surveyResultModel: SurveyResultModel, @Query("LinkId") linkId: String): Call<JsonObject>

    @GET("UniqueLink/UniqueLinkId")
    suspend fun getNumberForGuidLink(@Query("UniqueLinkId") id: String): Response<Int>

}



