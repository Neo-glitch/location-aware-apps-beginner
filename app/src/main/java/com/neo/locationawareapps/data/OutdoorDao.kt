package com.neo.locationawareapps.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.neo.locationawareapps.data.*

@Dao
interface OutdoorDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLocations(locations: List<Location>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertActivities(activities: List<Activity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertActivityLocationCrossRefs(activityLocationCrossRefs: List<ActivityLocationCrossRef>)

    @Query("SELECT * FROM Activity ORDER BY title")
    fun getAllActivities(): LiveData<List<Activity>>

    @Query("SELECT * FROM Location")
    fun getAllLocations(): LiveData<List<Location>>

    @Transaction
    @Query("SELECT * FROM Activity WHERE activityId = :activityId")
    fun getActivityWithLocations(activityId: Int): LiveData<ActivityWithLocations>

    @Query("SELECT * FROM Location WHERE locationId = :locationId")
    fun getLocationById(locationId: Int): Location

    @Transaction
    @Query("SELECT DISTINCT L.* FROM Location L, Activity A, ActivityLocationCrossRef AL WHERE L.locationId = AL.locationId AND AL.activityId = A.activityId AND A.geofenceEnabled != 0")
    fun getLocationsForGeofencing(): List<Location>

    @Transaction
    @Query("SELECT * FROM Location WHERE locationId = :locationId")
    fun getLocationWithActivities(locationId: Int): LiveData<LocationWithActivities>

    @Query("UPDATE activity set geofenceEnabled = ~geofenceEnabled WHERE activityId = :id")
    fun toggleGeofenceEnabled(id: Int): Int
}