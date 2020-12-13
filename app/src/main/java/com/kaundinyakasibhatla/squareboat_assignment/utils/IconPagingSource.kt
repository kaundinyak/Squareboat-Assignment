package com.kaundinyakasibhatla.squareboat_assignment.utils


import androidx.paging.PagingSource
import com.kaundinyakasibhatla.squareboat_assignment.data.model.Format
import com.kaundinyakasibhatla.squareboat_assignment.data.repository.MainRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

 class IconPagingSource @Inject constructor(
    private val mainRepository: MainRepository
) : PagingSource<Int, Format>() {

     override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Format> {
         val offset = params.key ?: 1
         return try {
             val response = mainRepository.getIcons(20,offset)
             val format = response.body()?.let {
                 it.icons?.mapNotNull { icon ->
                     icon.raster_sizes?.firstOrNull { rasterSize ->
                         rasterSize.size == 512
                     }?.formats?.first()
                 }
             }

             LoadResult.Page(
                 data = format!!,
                 prevKey = if (offset == 1) null else offset - 1,
                 nextKey = offset + 1
             )
         } catch (exception: IOException) {
             LoadResult.Error(exception)
         } catch (exception: HttpException) {
             LoadResult.Error(exception)
         }

     }

}