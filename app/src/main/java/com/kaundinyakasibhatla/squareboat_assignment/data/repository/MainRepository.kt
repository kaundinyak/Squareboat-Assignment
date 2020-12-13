package com.kaundinyakasibhatla.squareboat_assignment.data.repository

import com.kaundinyakasibhatla.squareboat_assignment.data.api.ApiHelper
import javax.inject.Inject

class MainRepository @Inject constructor(private val apiHelper: ApiHelper) {

    suspend fun getIcons(count:Int, offset:Int) =  apiHelper.getIcons(count, offset)
    suspend fun getSearchIcons(query:String,count:Int, offset:Int) = apiHelper.getSearchIcons(query, count,offset)

}