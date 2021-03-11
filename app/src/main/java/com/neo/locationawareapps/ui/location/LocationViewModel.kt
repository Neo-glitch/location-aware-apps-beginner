package com.neo.locationawareapps.ui.location

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.neo.locationawareapps.data.OutdoorRepository
import com.neo.locationawareapps.data.OutdoorRoomDatabase
import com.neo.locationawareapps.data.OutdoorRoomRepository

class LocationViewModel(application: Application) : AndroidViewModel(application) {
    private val outdoorRepository: OutdoorRepository

    init {
        val outdoorDao = OutdoorRoomDatabase.getInstance(application).outdoorDao()
        outdoorRepository = OutdoorRoomRepository(outdoorDao)
    }

    fun getLocation(locationId: Int) =
        outdoorRepository.getLocationWithActivities(locationId)
}