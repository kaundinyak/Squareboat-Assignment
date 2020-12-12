package com.kaundinyakasibhatla.squareboat_assignment.ui.main.view

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.kaundinyakasibhatla.squareboat_assignment.R
import com.kaundinyakasibhatla.squareboat_assignment.data.model.Format
import com.kaundinyakasibhatla.squareboat_assignment.databinding.ActivityMainBinding
import com.kaundinyakasibhatla.squareboat_assignment.ui.main.adapter.MainAdapter
import com.kaundinyakasibhatla.squareboat_assignment.ui.main.viewmodel.MainActivityViewModel
import com.kaundinyakasibhatla.squareboat_assignment.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    val WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 125
    private lateinit var binding: ActivityMainBinding
    private val mainActivityViewModel: MainActivityViewModel by viewModels()
    private lateinit var adapter: MainAdapter
    private var downloadUrl: String? = null

    @FlowPreview
    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setupUI()
        setupObserver()
    }


    @ExperimentalCoroutinesApi
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        val search = menu.findItem(R.id.search)
        val searchView = search.actionView as SearchView
        searchView.queryHint = "Search"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                //Toast.makeText(this@MainActivity, query, Toast.LENGTH_SHORT).show()
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                //adapter.filter.filter(newText)
                lifecycleScope.launch {
                    mainActivityViewModel.queryChannel.send(newText.toString())
                }
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)

    }

    private fun setupUI() {
        binding.recyclerView.layoutManager = GridLayoutManager(this, 2)
        adapter = MainAdapter(arrayListOf(), ::showDialog)
        binding.recyclerView.adapter = adapter

    }

    private fun showDialog(url: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Download Icon")
        builder.setMessage("Do you want to download the selected icon?")
        builder.setPositiveButton("Download") { dialog, which ->
            downloadUrl = url
            if(checkStoragePermission()){
                mainActivityViewModel.downloadImage(url)

            }else{
                requestStoragePermissions()
            }
        }

        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.dismiss()
        }

        builder.show()
    }


    @FlowPreview
    @ExperimentalCoroutinesApi
    private fun setupObserver() {
        mainActivityViewModel.icons.observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    binding.progressBar.visibility = View.GONE
                    it.data?.let { icons -> renderList(icons) }
                    binding.recyclerView.visibility = View.VISIBLE
                }
                Status.LOADING -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE
                }
                Status.ERROR -> {
                    //Handle Error
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
            }
        })

        mainActivityViewModel.searchResult.observe(this, Observer {
            print(it)
        })

        mainActivityViewModel.downloadStarted.observe(this, Observer {
            if (it == true) {
                binding.progressBar.visibility = View.VISIBLE
                Toast.makeText(this, "Please Wait", Toast.LENGTH_SHORT).show()
            }

        })

        mainActivityViewModel.downloadCompleted.observe(this, Observer {
            if (it == true) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, "Icon Downloaded", Toast.LENGTH_SHORT).show()
            }

        })

    }


    private fun renderList(icons: List<Format>) {
        adapter.clearData(icons)
        adapter.addData(icons)
        adapter.notifyDataSetChanged()
    }


    private fun checkStoragePermission(): Boolean {
        if (this.let {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            }
            != PackageManager.PERMISSION_GRANTED) {
            return false
        }
        return true
    }

    private fun requestStoragePermissions() {
        this.let {
            ActivityCompat.requestPermissions(
                it,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), WRITE_EXTERNAL_STORAGE_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode) {
            WRITE_EXTERNAL_STORAGE_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    downloadUrl?.let { mainActivityViewModel.downloadImage(it) }
                } else {
                    Snackbar.make(binding.layoutMain, "Storage permission is required to download", Snackbar.LENGTH_SHORT).show()
                }
                return
            }
        }
    }


}