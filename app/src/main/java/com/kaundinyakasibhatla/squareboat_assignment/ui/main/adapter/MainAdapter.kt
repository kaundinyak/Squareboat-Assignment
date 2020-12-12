package com.kaundinyakasibhatla.squareboat_assignment.ui.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kaundinyakasibhatla.squareboat_assignment.R
import com.kaundinyakasibhatla.squareboat_assignment.data.model.Format
import com.kaundinyakasibhatla.squareboat_assignment.data.model.Icon
import kotlinx.android.synthetic.main.item_layout.view.*

class MainAdapter(
    private val icons: ArrayList<Format>,
    private val listener:(String)->Unit,
) : RecyclerView.Adapter<MainAdapter.DataViewHolder>() {

    class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(icon: Format) {

            Glide.with(itemView.imageViewIcon.context)
                .load(icon.preview_url)
                .into(itemView.imageViewIcon)


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        DataViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_layout, parent,
                false
            )
        )

    override fun getItemCount(): Int = icons.size

    override fun onBindViewHolder(holder: DataViewHolder, position: Int){
        holder.bind(icons[position])
        holder.itemView.setOnClickListener {
            listener(icons[position].preview_url!!)
        }

    }

    fun addData(list: List<Format>) {
        icons.addAll(list)
    }

    fun clearData(list: List<Format>) {
        icons.clear()
    }
}