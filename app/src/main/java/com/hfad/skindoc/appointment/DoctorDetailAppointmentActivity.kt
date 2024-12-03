package com.hfad.skindoc.appointment

import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.TextView
import java.text.SimpleDateFormat
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.hfad.skindoc.R
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.Calendar
import java.util.Locale

class DoctorDetailAppointmentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.appointment_layout)


        val doctorName = intent.getStringExtra("doctor_name") ?: "Unknown"
        val doctorCity = intent.getStringExtra("doctor_clinic") ?: "Bengaluru"



        findViewById<TextView>(R.id.doctorName).text = doctorName
        findViewById<TextView>(R.id.doctorCity).text = doctorCity

        val aboutdoc = findViewById<TextView>(R.id.aboutText)
        val doctorSpecialty = "Dermatologist"
        val doctorExperience = 24

        aboutdoc.text = "$doctorName is a highly skilled and compassionate $doctorSpecialty with $doctorExperience years of experience in treating a wide spectrum of skin, hair, and nail conditions. Known for their patient-first approach, $doctorName stays at the forefront of dermatological advancements, ensuring the best care for every individual. With expertise in acne treatment, anti-aging solutions, and skin cancer management, they are dedicated to helping patients achieve healthy, radiant skin. $doctorName believes in empowering patients with knowledge and personalized treatment plans tailored to their unique needs.\n"


        val calendarRecyclerView = findViewById<RecyclerView>(R.id.calendarRecyclerView)
        val calendarItems = generateUpcomingDays()
        val adapter = CalendarCardAdapter(calendarItems) { selectedItem ->


        }

        calendarRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        calendarRecyclerView.adapter = adapter

        val timeSlots = listOf(
            R.id.time_9_am,
            R.id.time_10_am,
            R.id.time_11_am,
            R.id.time_1_pm,
            R.id.time_2_pm,
            R.id.time_3_pm,
            R.id.time_4_pm,
            R.id.time_7_pm,
            R.id.time_8_pm
        )

        for (timeSlotId in timeSlots) {
            val timeSlotView = findViewById<TextView>(timeSlotId)
            timeSlotView.setOnClickListener { onTimeSlotClicked(timeSlotView) }
        }
    }

    private fun onTimeSlotClicked(selectedView: TextView) {
        val timeSlots = listOf(
            R.id.time_9_am,
            R.id.time_10_am,
            R.id.time_11_am,
            R.id.time_1_pm,
            R.id.time_2_pm,
            R.id.time_3_pm,
            R.id.time_4_pm,
            R.id.time_7_pm,
            R.id.time_8_pm
        )

        for (timeSlotId in timeSlots) {
            val timeSlotView = findViewById<TextView>(timeSlotId)
            timeSlotView.backgroundTintList = null
            timeSlotView.setTextColor(ContextCompat.getColor(this, R.color.black))
        }

        val tintColor = ContextCompat.getColor(this, R.color.cale)
        val textColor = ContextCompat.getColor(this, R.color.white)

        selectedView.backgroundTintList = ColorStateList.valueOf(tintColor)
        selectedView.setTextColor(textColor)
    }


    private fun generateUpcomingDays(): List<CalendarCardItem> {
        val calendar = Calendar.getInstance()
        val items = mutableListOf<CalendarCardItem>()


        val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
        val dateFormat = SimpleDateFormat("d", Locale.getDefault())


        for (i in 0..6) {
            val day = dayFormat.format(calendar.time)
            val date = dateFormat.format(calendar.time)


            items.add(CalendarCardItem(day, date))


            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        return items
    }
}

