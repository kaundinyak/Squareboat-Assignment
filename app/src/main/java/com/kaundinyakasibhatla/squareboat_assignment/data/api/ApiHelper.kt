package com.kaundinyakasibhatla.squareboat_assignment.data.api

import com.kaundinyakasibhatla.squareboat_assignment.data.model.Icon
import com.kaundinyakasibhatla.squareboat_assignment.data.model.IconsModel
import com.kaundinyakasibhatla.squareboat_assignment.data.model.Results
import retrofit2.Response

interface ApiHelper {
    suspend fun  getIcons(): Response<IconsModel>
    suspend fun getSearchIcons(query:String,count:Int): Response<IconsModel>
}