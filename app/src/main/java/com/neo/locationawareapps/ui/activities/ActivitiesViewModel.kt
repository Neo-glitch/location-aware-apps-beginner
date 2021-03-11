package com.neo.locationawareapps.ui.activities

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.neo.locationawareapps.data.OutdoorRepository
import com.neo.locationawareapps.data.OutdoorRoomDatabase
import com.neo.locationawareapps.data.OutdoorRoomRepository

class ActivitiesViewModel(application: Application) : AndroidViewModel(application) {
    private val outdoorRepository: OutdoorRepository

    init {
        val outdoorDao = OutdoorRoomDatabase.getInstance(application).outdoorDao()
        outdoorRepository = OutdoorRoomRepository(outdoorDao)
    }

    val allActivities = outdoorRepository.getAllActivities()

    fun toggleGeofencing(id: Int) = outdoorRepository.toggleActivityGeofence(id)  // enables or disable geofencing of a location
}