package com.hfad.skindoc.appointment

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hfad.skindoc.R

class CalendarCardAdapter(
    private val items: List<CalendarCardItem>,
    private val onItemClick: (CalendarCardItem) -> Unit
) : RecyclerView.Adapter<CalendarCardAdapter.ViewHolder>() {

    private var selectedPosition: Int = -1

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dayTextView: TextView = itemView.findViewById(R.id.dayText)
        private val dateTextView: TextView = itemView.findViewById(R.id.dateText)
        private val linearLayout: LinearLayout = itemView.findViewById(R.id.calendarLayout)

        fun bind(item: CalendarCardItem, position: Int) {
            dayTextView.text = item.day
            dateTextView.text = item.date


            if (position == selectedPosition) {
                linearLayout.backgroundTintList =
                    itemView.context.getColorStateList(R.color.blue)
                dayTextView.setTextColor(itemView.context.getColor(R.color.white))
                dateTextView.setTextColor(itemView.context.getColor(R.color.white))
            } else {
                linearLayout.backgroundTintList = null
                dayTextView.setTextColor(itemView.context.getColor(R.color.black))
                dateTextView.setTextColor(itemView.context.getColor(R.color.black))
            }


            itemView.setOnClickListener {
                val previousPosition = selectedPosition
                selectedPosition = position

                notifyItemChanged(previousPosition)
                notifyItemChanged(selectedPosition)

                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.calendar_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], position)
    }

    override fun getItemCount(): Int = items.size
}
