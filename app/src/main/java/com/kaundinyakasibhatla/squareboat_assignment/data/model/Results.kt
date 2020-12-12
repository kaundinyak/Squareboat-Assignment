package com.kaundinyakasibhatla.squareboat_assignment.data.model
import com.squareup.moshi.Json

data class Results<T>(
    val total_count:Int, val  results:List<T>
)