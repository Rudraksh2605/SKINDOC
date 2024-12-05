package com.hfad.skindoc.event

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hfad.skindoc.R

class EventsAdapter(private val events: List<Event>) :
    RecyclerView.Adapter<EventsAdapter.EventViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.event_card, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]
        holder.bind(event)
    }

    override fun getItemCount(): Int = events.size

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val doctorNameTextView: TextView = itemView.findViewById(R.id.tv_doctor_name)
        private val appointmentDateTextView: TextView = itemView.findViewById(R.id.tv_appointment_date)
        private val appointmentTimeTextView: TextView = itemView.findViewById(R.id.tv_appointment_time)
        private val doctorCityTextView: TextView = itemView.findViewById(R.id.tv_doctor_city)

        fun bind(event: Event) {
            doctorNameTextView.text = event.doctorName
            appointmentDateTextView.text = "Date: ${event.appointmentDate}"
            appointmentTimeTextView.text = "Time: ${event.appointmentTime}"
            doctorCityTextView.text = "City: ${event.doctorCity}"
        }
    }
}
