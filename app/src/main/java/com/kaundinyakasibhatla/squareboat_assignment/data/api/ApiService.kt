package com.kaundinyakasibhatla.squareboat_assignment.data.api

import com.kaundinyakasibhatla.squareboat_assignment.data.model.IconsModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("iconsets/180041/icons?count=30")
    suspend fun getIcons(): Response<IconsModel>

    @GET("icons/search")
    suspend fun getSearchIcons(@Query("query") query: String?, @Query("count") count: Int): Response<IconsModel>

}