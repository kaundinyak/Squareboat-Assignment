package com.kaundinyakasibhatla.squareboat_assignment.data.api

import com.kaundinyakasibhatla.squareboat_assignment.data.model.Icon
import com.kaundinyakasibhatla.squareboat_assignment.data.model.IconsModel
import com.kaundinyakasibhatla.squareboat_assignment.data.model.Results
import retrofit2.Response
import javax.inject.Inject

class ApiHelperImpl @Inject constructor(private val apiService: ApiService) : ApiHelper {

    override suspend fun getIcons(): Response<IconsModel> = apiService.getIcons()
    override suspend fun getSearchIcons(query: String, count:Int ): Response<IconsModel> = apiService.getSearchIcons(query, count)
}