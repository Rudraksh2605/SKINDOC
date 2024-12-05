package com.hfad.skindoc.event

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.hfad.skindoc.R

class EventsActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var eventsList: ArrayList<Event>
    private lateinit var eventsAdapter: EventsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.events)


        val events: RecyclerView = findViewById(R.id.events)


        database = FirebaseDatabase.getInstance().getReference("Appointments")


        eventsList = ArrayList()
        eventsAdapter = EventsAdapter(eventsList)


        events.layoutManager = LinearLayoutManager(this)
        events.adapter = eventsAdapter


        fetchEventsFromFirebase()
    }

    private fun fetchEventsFromFirebase() {

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                eventsList.clear()
                for (eventSnapshot in snapshot.children) {
                    val event = eventSnapshot.getValue(Event::class.java)
                    if (event != null) {
                        eventsList.add(event)
                    }
                }
                eventsAdapter.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}
