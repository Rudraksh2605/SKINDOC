package com.hfad.skindoc.home

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hfad.skindoc.R
import com.hfad.skindoc.appointment.DoctorDetailAppointmentActivity

class DoctorAdapter(private val doctorList: List<DoctorDataModel>) :
    RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.doctor_card, parent, false)
        return DoctorViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DoctorViewHolder, position: Int) {
        val doctor = doctorList[position]
        holder.tvDoctorName.text = doctor.name
        holder.tvClinicName.text = doctor.clinic
        holder.tvAddress.text = doctor.address
        holder.tvContact.text = doctor.contact

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DoctorDetailAppointmentActivity::class.java)
            intent.putExtra("doctor_name", doctor.name)
            intent.putExtra("doctor_clinic", doctor.clinic)
            intent.putExtra("doctor_address", doctor.address)
            intent.putExtra("doctor_contact", doctor.contact)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return doctorList.size
    }

    class DoctorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDoctorName: TextView = itemView.findViewById(R.id.tv_doctor_name)
        val tvClinicName: TextView = itemView.findViewById(R.id.tv_clinic_name)
        val tvAddress: TextView = itemView.findViewById(R.id.tv_address)
        val tvContact: TextView = itemView.findViewById(R.id.tv_contact)
    }
}
