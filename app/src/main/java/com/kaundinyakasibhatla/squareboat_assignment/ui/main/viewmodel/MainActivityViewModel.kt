package com.kaundinyakasibhatla.squareboat_assignment.ui.main.viewmodel

import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.kaundinyakasibhatla.squareboat_assignment.BuildConfig
import com.kaundinyakasibhatla.squareboat_assignment.data.model.Format
import com.kaundinyakasibhatla.squareboat_assignment.data.repository.MainRepository
import com.kaundinyakasibhatla.squareboat_assignment.utils.NetworkHelper
import com.kaundinyakasibhatla.squareboat_assignment.utils.Resource
import com.tonyodev.fetch2.*
import com.tonyodev.fetch2.NetworkType
import com.tonyodev.fetch2core.DownloadBlock
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import java.io.File


class MainActivityViewModel @ViewModelInject constructor(
    private val mainRepository: MainRepository,
    private val networkHelper: NetworkHelper,
    @ApplicationContext private val context: Context
) : ViewModel(), FetchListener {

    companion object {
        const val SEARCH_DELAY_MS = 300L
        const val MIN_QUERY_LENGTH = 3
    }

    private var fetchConfiguration: FetchConfiguration = FetchConfiguration.Builder(context)
        .setDownloadConcurrentLimit(3)
        .build()
    var fetch: Fetch

    init {
        fetch = Fetch.Impl.getInstance(fetchConfiguration)
        fetch.addListener(this)
    }

    override fun onCleared() {
        super.onCleared()
        fetch.removeListener(this)
    }

    @ExperimentalCoroutinesApi
    internal val queryChannel = BroadcastChannel<String>(Channel.CONFLATED)
    var searchOffset : Int = 0

    @FlowPreview
    @ExperimentalCoroutinesApi
    internal val internalSearchResult = queryChannel
        .asFlow()
        .debounce(SEARCH_DELAY_MS)
        .mapLatest {
            if (it.length >= MIN_QUERY_LENGTH) {
                if (networkHelper.isNetworkConnected()) {

                    val response = mainRepository.getSearchIcons(it, 20, searchOffset)
                        if (response.isSuccessful) {
                            response.body()?.let { iconsModel ->

                                val iconsList = iconsModel.icons?.mapNotNull { icon ->
                                    icon.raster_sizes?.firstOrNull { rasterSize ->
                                        rasterSize.size == 512
                                    }?.formats?.first()
                                }
                                _icons.postValue(Resource.success(iconsList!! + icons.value?.data!!))
                                searchOffset += searchOffset

                            } ?: run {
                                _icons.postValue(Resource.success(icons.value?.data))

                            }

                        } else {
                            _icons.postValue(Resource.success(icons.value?.data))
                        }


                } else {
                    _icons.postValue(Resource.error("No internet connection", null))
                }
            }

        }

    @ExperimentalCoroutinesApi
    @FlowPreview
    val searchResult = internalSearchResult.asLiveData()
    val downloadStarted = MutableLiveData(false)
    val downloadCompleted = MutableLiveData(false)

    private val _icons = MutableLiveData<Resource<List<Format>>>()
    val icons: LiveData<Resource<List<Format>>>
        get() = _icons


    fun fetchIcons(offset: Int) {
        if (offset == 0) {
            _icons.postValue(Resource.loading(emptyList()))
        } else {
            _icons.postValue(Resource.loading(icons.value?.data))
        }
        viewModelScope.launch {

            if (networkHelper.isNetworkConnected()) {

                val response = mainRepository.getIcons(20, offset)
                if (response.isSuccessful) {
                    response.body()?.let {

                        val iconsList = it.icons?.mapNotNull { icon ->
                            icon.raster_sizes?.firstOrNull { rasterSize ->
                                rasterSize.size == 512
                            }?.formats?.first()
                        }
                        _icons.postValue(Resource.success(iconsList!! + icons.value?.data!!))


                    } ?: run {
                        _icons.postValue(Resource.error(response.errorBody().toString(), null))
                    }

                } else {
                    _icons.postValue(Resource.error(response.errorBody().toString(), null))
                }

            } else _icons.postValue(Resource.error("No internet connection", null))
        }
    }

    fun downloadImage(url: String) {
        val directory = Environment.getExternalStorageDirectory().absolutePath + "/Images"
        val name = url.split('/').last()
        val file = File(directory, name)
        val request = Request(url, file.absolutePath)
        request.priority = Priority.HIGH
        request.networkType = NetworkType.ALL
        request.addHeader("Authorization", BuildConfig.client_secret)

        fetch.enqueue(request,
            { updatedRequest: Request? -> Log.e("adsf", "asdfas") }
        ) { error: Error? -> Log.e("adsf", "asdfas") }

    }

    override fun onAdded(download: Download) {
        Log.e("FETCH", "onAdded")
    }

    override fun onCancelled(download: Download) {

    }

    override fun onCompleted(download: Download) {
        Log.e("FETCH", "onCompleted")
        downloadCompleted.value = true
    }

    override fun onDeleted(download: Download) {

    }

    override fun onDownloadBlockUpdated(
        download: Download,
        downloadBlock: DownloadBlock,
        totalBlocks: Int
    ) {

    }

    override fun onError(download: Download, error: Error, throwable: Throwable?) {
        Log.e("FETCH", "onError")
        downloadCompleted.value = true
    }

    override fun onPaused(download: Download) {

    }

    override fun onProgress(
        download: Download,
        etaInMilliSeconds: Long,
        downloadedBytesPerSecond: Long
    ) {

        Log.e("FETCH", "onProgress")
    }

    override fun onQueued(download: Download, waitingOnNetwork: Boolean) {

    }

    override fun onRemoved(download: Download) {

    }

    override fun onResumed(download: Download) {

    }

    override fun onStarted(
        download: Download,
        downloadBlocks: List<DownloadBlock>,
        totalBlocks: Int
    ) {
        Log.e("FETCH", "onStarted")
        downloadStarted.value = true
    }

    override fun onWaitingNetwork(download: Download) {

    }


}

