package com.neo.locationawareapps.data

import androidx.room.*
import com.google.android.gms.location.Geofence

@Entity
data class Activity(
    @PrimaryKey(autoGenerate = true) val activityId: Int = 0,
    val title: String,
    val icon: String,
    val geofenceEnabled: Boolean
)

@Entity
data class Location(
    @PrimaryKey(autoGenerate = true) val locationId: Int = 0,
    val title: String,
    val description: String,
    val hours: String,
    val latitude: Double,
    val longitude: Double,
    val geofenceRadius: Float
) {
    fun getDistanceInMiles(currentLocation: android.location.Location): Float {
        val coordinates = android.location.Location("")
        coordinates.latitude = latitude
        coordinates.longitude = longitude
        val meters = currentLocation.distanceTo(coordinates)
        return meters / 1609f
    }
}


/*
entity class to establish relationship btw an activity and location
 */
@Entity(primaryKeys = ["activityId", "locationId"])
data class ActivityLocationCrossRef(
    val activityId: Int,
    val locationId: Int
)

data class ActivityWithLocations(
    @Embedded val activity: Activity,
    @Relation(
        parentColumn = "activityId",
        entityColumn = "locationId",
        associateBy = Junction(ActivityLocationCrossRef::class)
    )
    val locations: List<Location>
)

data class LocationWithActivities(
    @Embedded val location: Location,
    @Relation(
        parentColumn = "locationId",
        entityColumn = "activityId",
        associateBy = Junction(ActivityLocationCrossRef::class)
    )
    val activities: List<Activity>
)

class GeofencingChanges(val idsToRemove: List<String>, val locationsToAdd: List<Geofence>)