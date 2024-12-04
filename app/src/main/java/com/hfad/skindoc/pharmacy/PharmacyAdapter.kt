package com.hfad.skindoc.pharmacy

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hfad.skindoc.R

class PharmacyAdapter(private val pharmacyList: List<PharmacyDataModel>) :
    RecyclerView.Adapter<PharmacyAdapter.PharmacyViewHolder>() {

    inner class PharmacyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvPharmacyName)
        val tvPrice: TextView = itemView.findViewById(R.id.tvPharmacyPrice)
        val tvFeatures: TextView = itemView.findViewById(R.id.tvPharmacyFeatures)
        val tvBestFor: TextView = itemView.findViewById(R.id.tvPharmacyBestFor)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PharmacyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.pharmacy_card, parent, false)
        return PharmacyViewHolder(view)
    }

    override fun onBindViewHolder(holder: PharmacyViewHolder, position: Int) {
        val pharmacy = pharmacyList[position]
        holder.tvName.text = pharmacy.name
        holder.tvPrice.text = "Price: ${pharmacy.price}"
        holder.tvFeatures.text = "Features: ${pharmacy.features}"
        holder.tvBestFor.text = "Best For: ${pharmacy.bestFor}"
    }

    override fun getItemCount(): Int {
        return pharmacyList.size
    }
}
