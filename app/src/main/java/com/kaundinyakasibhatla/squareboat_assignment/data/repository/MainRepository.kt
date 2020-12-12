package com.kaundinyakasibhatla.squareboat_assignment.data.repository

import com.kaundinyakasibhatla.squareboat_assignment.data.api.ApiHelper
import javax.inject.Inject

class MainRepository @Inject constructor(private val apiHelper: ApiHelper) {

    suspend fun getIcons() =  apiHelper.getIcons()
    suspend fun getSearchIcons(query:String,count:Int) = apiHelper.getSearchIcons(query, count)

}