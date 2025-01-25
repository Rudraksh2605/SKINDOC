package com.hfad.skindoc.appointment

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.hfad.skindoc.R
import com.hfad.skindoc.chatbot.ChatBotActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DoctorDetailAppointmentActivity : AppCompatActivity() {

    private var selectedDate: String? = null
    private var selectedTime: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.appointment_layout)

        getCurrentUserUid()

        val doctorName = intent.getStringExtra("doctor_name") ?: "Unknown"
        val doctorCity = intent.getStringExtra("doctor_clinic") ?: "Bengaluru"

        findViewById<TextView>(R.id.doctorName).text = doctorName
        findViewById<TextView>(R.id.doctorCity).text = doctorCity
        val bot = findViewById<ImageButton>(R.id.chat_bot)
        bot.setOnClickListener {
            val intent = Intent(this, ChatBotActivity::class.java)
            startActivity(intent)
        }

        val aboutDoc = findViewById<TextView>(R.id.aboutText)
        val doctorSpecialty = "Dermatologist"
        val doctorExperience = 24

        aboutDoc.text = "$doctorName is a highly skilled and compassionate $doctorSpecialty with $doctorExperience years of experience in treating a wide spectrum of skin, hair, and nail conditions. Known for their patient-first approach, $doctorName stays at the forefront of dermatological advancements, ensuring the best care for every individual. With expertise in acne treatment, anti-aging solutions, and skin cancer management, they are dedicated to helping patients achieve healthy, radiant skin. $doctorName believes in empowering patients with knowledge and personalized treatment plans tailored to their unique needs."

        val calendarRecyclerView = findViewById<RecyclerView>(R.id.calendarRecyclerView)
        val calendarItems = generateUpcomingDays()
        val adapter = CalendarCardAdapter(calendarItems) { selectedItem ->
            selectedDate = selectedItem.date
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

        findViewById<Button>(R.id.bookAppointmentButton).setOnClickListener {
            bookAppointment(doctorName, doctorCity)
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
        selectedTime = selectedView.text.toString()
    }

    private fun bookAppointment(doctorName: String, doctorCity: String) {
        if (selectedDate == null || selectedTime == null) {
            Toast.makeText(this, "Please select a date and time.", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = getCurrentUserUid()
        if (userId == null) {
            Toast.makeText(this, "Failed to get user ID. Please try again.", Toast.LENGTH_SHORT).show()
            return
        }

        val appointmentDetails = mapOf(
            "userId" to userId,
            "doctorName" to doctorName,
            "doctorCity" to doctorCity,
            "appointmentDate" to selectedDate,
            "appointmentTime" to selectedTime
        )

        val databaseReference = FirebaseDatabase.getInstance().getReference("Appointments")
        databaseReference.push().setValue(appointmentDetails)
            .addOnSuccessListener {
                Toast.makeText(this, "Appointment booked successfully!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { error ->
                Toast.makeText(this, "Failed to book appointment: ${error.message}", Toast.LENGTH_SHORT).show()
            }
    }


    fun getCurrentUserUid(): String? {
        val currentUser = FirebaseAuth.getInstance().currentUser
        return currentUser?.uid
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
