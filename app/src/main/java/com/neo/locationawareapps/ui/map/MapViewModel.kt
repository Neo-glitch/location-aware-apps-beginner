package com.neo.locationawareapps.ui.map

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.neo.locationawareapps.data.OutdoorRepository
import com.neo.locationawareapps.data.OutdoorRoomDatabase
import com.neo.locationawareapps.data.OutdoorRoomRepository

class MapViewModel(application: Application) : AndroidViewModel(application) {
    private val outdoorRepository: OutdoorRepository

    init {
        val outdoorDao = OutdoorRoomDatabase.getInstance(application).outdoorDao()
        outdoorRepository = OutdoorRoomRepository(outdoorDao)
    }

    /*
    ret live data list of locations
     */
    val allLocations = outdoorRepository.getAllLocations()
}