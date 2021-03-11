package com.neo.locationawareapps.data

import android.os.AsyncTask
import com.google.android.gms.location.Geofence

class OutdoorRoomRepository(private val outdoorDao: OutdoorDao) : OutdoorRepository {

    override fun getAllActivities() = outdoorDao.getAllActivities()

    override fun getAllLocations() = outdoorDao.getAllLocations()

    override fun getActivityWithLocations(activityId: Int) =
        outdoorDao.getActivityWithLocations(activityId)

    override fun getLocationById(locationId: Int): Location =
        GetLocationAsyncTask(outdoorDao).execute(locationId).get()

    override fun getLocationWithActivities(locationId: Int) =
        outdoorDao.getLocationWithActivities(locationId)

    override fun toggleActivityGeofence(id: Int): GeofencingChanges =
        ToggleAsyncTask(outdoorDao).execute(id).get()

    private class GetLocationAsyncTask(val outdoorDao: OutdoorDao) :
        AsyncTask<Int, Unit, Location>() {
        override fun doInBackground(vararg ids: Int?): Location {
            return outdoorDao.getLocationById(ids[0]!!)
        }
    }

    /**
     * gets delta of geofence changes, by creating a list of geofences to remove and
     * creates a list of new geofences too
     */
    private class ToggleAsyncTask(val outdoorDao: OutdoorDao) :
        AsyncTask<Int, Unit, GeofencingChanges>() {
        override fun doInBackground(vararg ids: Int?): GeofencingChanges {
            val previousLocations = outdoorDao.getLocationsForGeofencing()
            require(outdoorDao.toggleGeofenceEnabled(ids[0]!!) == 1) { "Activity not found" }
            val newLocations = outdoorDao.getLocationsForGeofencing()

            val removedLocations = previousLocations.subtract(newLocations)
            val addedLocations = newLocations.subtract(previousLocations)

            return GeofencingChanges(
                removedLocations.map { l -> l.locationId.toString() },  // geofences list to remove
                addedLocations.map { l -> createGeofence(l) }           // new geofences list to add
            )
        }

        /*
        creates the geofence
         */
        private fun createGeofence(location: Location): Geofence {
            return Geofence.Builder()
                .setRequestId(location.locationId.toString())
                .setCircularRegion(
                    location.latitude,
                    location.longitude,
                    location.geofenceRadius
                )
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build()
        }
    }
}