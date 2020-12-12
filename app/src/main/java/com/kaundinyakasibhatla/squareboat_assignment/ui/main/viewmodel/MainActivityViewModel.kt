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

    private var fetchConfiguration: FetchConfiguration = FetchConfiguration.Builder(context)
        .setDownloadConcurrentLimit(3)
        .build()
    var fetch: Fetch
    companion object {
        const val SEARCH_DELAY_MS = 300L
        const val MIN_QUERY_LENGTH = 3
    }

    init {

        fetch = Fetch.Impl.getInstance(fetchConfiguration)
        fetch.addListener(this)
    }

    @ExperimentalCoroutinesApi
    internal val queryChannel = BroadcastChannel<String>(Channel.CONFLATED)

    @FlowPreview
    @ExperimentalCoroutinesApi
    internal val internalSearchResult = queryChannel
        .asFlow()
        .debounce(SEARCH_DELAY_MS)
        .mapLatest {
            if (it.length >= MIN_QUERY_LENGTH) {
                if (networkHelper.isNetworkConnected()) {
                    mainRepository.getSearchIcons(it, 24).let { response ->
                        if (response.isSuccessful) {
                            response.body()?.let { iconsModel ->

                                _icons.postValue(Resource.success((iconsModel.icons?.mapNotNull { icon ->
                                    icon.raster_sizes?.firstOrNull { rasterSize ->
                                        rasterSize.size == 512
                                    }?.formats?.first()
                                })))
                            } ?: run {
                                _icons.postValue(
                                    Resource.error(
                                        response.errorBody().toString(),
                                        null
                                    )
                                )
                            }

                        } else {
                            _icons.postValue(Resource.error(response.errorBody().toString(), null))
                        }

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

    init {
        fetchIcons()
    }



    fun downloadImage(url: String) {
        val directory = Environment.getExternalStorageDirectory().absolutePath+"/Images"
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

    private fun fetchIcons() {
        viewModelScope.launch {
            _icons.postValue(Resource.loading(null))
            if (networkHelper.isNetworkConnected()) {
                mainRepository.getIcons().let { response ->
                    if (response.isSuccessful) {
                        response.body()?.let {

                            _icons.postValue(Resource.success((it.icons?.mapNotNull { icon ->
                                icon.raster_sizes?.firstOrNull { rasterSize ->
                                    rasterSize.size == 512
                                }?.formats?.first()
                            })))
                        } ?: run {
                            _icons.postValue(Resource.error(response.errorBody().toString(), null))
                        }

                    } else {
                        _icons.postValue(Resource.error(response.errorBody().toString(), null))
                    }
                }
            } else _icons.postValue(Resource.error("No internet connection", null))
        }
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

