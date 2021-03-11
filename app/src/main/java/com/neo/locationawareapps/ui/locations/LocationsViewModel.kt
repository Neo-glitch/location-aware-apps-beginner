package com.neo.locationawareapps.ui.locations

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.neo.locationawareapps.data.OutdoorRepository
import com.neo.locationawareapps.data.OutdoorRoomDatabase
import com.neo.locationawareapps.data.OutdoorRoomRepository

class LocationsViewModel(application: Application) : AndroidViewModel(application) {
    private val outdoorRepository: OutdoorRepository

    init {
        val outdoorDao = OutdoorRoomDatabase.getInstance(application).outdoorDao()
        outdoorRepository = OutdoorRoomRepository(outdoorDao)
    }

    val allLocations = outdoorRepository.getAllLocations()

    fun locationsWithActivity(activityId: Int) =
        outdoorRepository.getActivityWithLocations(activityId)
}