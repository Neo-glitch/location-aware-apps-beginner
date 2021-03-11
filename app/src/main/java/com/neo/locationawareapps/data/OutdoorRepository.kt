package com.neo.locationawareapps.data

import androidx.lifecycle.LiveData
import com.neo.locationawareapps.data.*

interface OutdoorRepository {
    fun getAllActivities(): LiveData<List<Activity>>

    fun getAllLocations(): LiveData<List<Location>>

    fun getActivityWithLocations(activityId: Int): LiveData<ActivityWithLocations>

    fun getLocationById(locationId: Int): Location

    fun getLocationWithActivities(locationId: Int): LiveData<LocationWithActivities>

    fun toggleActivityGeofence(id: Int): GeofencingChanges
}