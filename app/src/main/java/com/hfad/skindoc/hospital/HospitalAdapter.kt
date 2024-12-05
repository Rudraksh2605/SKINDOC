package com.hfad.skindoc.hospital

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hfad.skindoc.R

class HospitalAdapter(
    private var hospitalList: List<Hospital>,
    private val context: Context
) : RecyclerView.Adapter<HospitalAdapter.HospitalViewHolder>() {

    class HospitalViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.hospital_name)
        val address: TextView = view.findViewById(R.id.hospital_address)
        val contact: TextView = view.findViewById(R.id.hospital_contact)
        val btnChrome: ImageButton = view.findViewById(R.id.open_browser_button)
        val btnCall: ImageButton = view.findViewById(R.id.call_button)
        val btnMaps: ImageButton = view.findViewById(R.id.direction_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HospitalViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_hospital_card, parent, false)
        return HospitalViewHolder(view)
    }

    override fun onBindViewHolder(holder: HospitalViewHolder, position: Int) {
        val hospital = hospitalList[position]
        holder.name.text = hospital.name
        holder.address.text = hospital.address
        holder.contact.text = hospital.contact

        holder.btnChrome.setOnClickListener {
            val searchQuery = Uri.encode(hospital.name)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=$searchQuery"))
            context.startActivity(intent)
            Log.d("DEBUG", "Opening browser for: ${hospital.name}")
        }

        holder.btnCall.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:${hospital.contact}")
            }
            context.startActivity(intent)
            Log.d("DEBUG", "Dialing number: ${hospital.contact}")
        }

        holder.btnMaps.setOnClickListener {
            val mapsUri = Uri.parse("geo:0,0?q=${Uri.encode(hospital.address)}")
            val intent = Intent(Intent.ACTION_VIEW, mapsUri).apply {
                setPackage("com.google.android.apps.maps")
            }
            context.startActivity(intent)
            Log.d("DEBUG", "Opening maps for: ${hospital.address}")
        }
    }

    override fun getItemCount(): Int = hospitalList.size

    fun updateHospitalList(newHospitalList: List<Hospital>) {
        hospitalList = newHospitalList
        notifyDataSetChanged()
    }
}
