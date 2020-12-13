package com.kaundinyakasibhatla.squareboat_assignment.ui.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kaundinyakasibhatla.squareboat_assignment.R
import com.kaundinyakasibhatla.squareboat_assignment.base.BaseRecyclerViewAdapter
import com.kaundinyakasibhatla.squareboat_assignment.data.model.Format
import com.kaundinyakasibhatla.squareboat_assignment.ui.main.helper.IconDiffCallback
import kotlinx.android.synthetic.main.item_layout.view.*

class MainAdapter(
    private val icons: List<Format>,
    private val listener:(String)->Unit,
) : BaseRecyclerViewAdapter<Format,MainAdapter.DataViewHolder>(icons.toMutableList()) {

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


    override fun onBindViewHolder(holder: DataViewHolder, position: Int){
        holder.bind(mList[position])
        holder.itemView.setOnClickListener {
            listener(mList[position].preview_url!!)
        }

    }

    override fun setData(list: List<Format>) {

        val diffCallback = IconDiffCallback(this.mList, list)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        mList.clear()
        mList.addAll(list)
        diffResult.dispatchUpdatesTo(this)
    }

}