package ru.markstudio.marksmarket.view

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.device_list_item.view.*
import ru.markstudio.marksmarket.R
import ru.markstudio.marksmarket.model.Device

class DeviceListAdapter(
        val context: Context,
        private val data: ArrayList<Device>,
        private val onItemClickListener: (Int) -> (Unit)
) : RecyclerView.Adapter<DeviceListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.device_list_item, parent, false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {
            val device = data[position]
            itemView.deviceNameTextView.text = device.name
            itemView.countTextView.text = "${device.count}"
            itemView.deviceImage.setImageResource(R.drawable.placeholder)
            itemView.arrowImage.setImageResource(R.drawable.ic_arrow_right)
            itemView.setOnClickListener { onItemClickListener(position) }
        }
    }
}