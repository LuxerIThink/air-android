package com.example.air_app.table

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.air_app.databinding.MeasurementViewBinding

class MeasurementsAdapter(private val mMeasurements: List<MeasurementViewModel>?) :
    RecyclerView.Adapter<MeasurementsAdapter.MeasurementViewHolder>() {

    inner class MeasurementViewHolder(binding: MeasurementViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val binding: MeasurementViewBinding = binding

        fun bind(measurement: MeasurementViewModel?) {
            binding.measurementViewModel = measurement
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeasurementsAdapter.MeasurementViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemBinding: MeasurementViewBinding =
            MeasurementViewBinding.inflate(layoutInflater, parent, false)
        return MeasurementViewHolder(itemBinding)
    }

    override fun onBindViewHolder(
        holder: MeasurementsAdapter.MeasurementViewHolder,
        position: Int
    ) {
        val measurement = mMeasurements!![position]
        holder.bind(measurement)
    }

    override fun getItemCount(): Int {
        return mMeasurements?.size ?: 0
    }
}