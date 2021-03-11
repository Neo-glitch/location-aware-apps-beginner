package com.neo.locationawareapps

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.neo.locationawareapps.data.OutdoorRepository
import com.neo.locationawareapps.data.OutdoorRoomDatabase
import com.neo.locationawareapps.data.OutdoorRoomRepository
import com.neo.locationawareapps.ui.location.LocationFragmentArgs

class GeofenceBroadcastReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val event = GeofencingEvent.fromIntent(intent)  // creates geofence event from intent

        if(event.hasError()){
            return
        }
        if(event.geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER){
            // check for geofence transition trigger
            val geofence = event.triggeringGeofences[0]
            sendNotification(context, geofence.requestId.toInt())
        }
    }

    private fun sendNotification(context: Context, locationId: Int) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel("locations",
            context.getString(R.string.notification_channel),
            NotificationManager.IMPORTANCE_DEFAULT)

            notificationManager.createNotificationChannel(channel)
        }

        // nav arch way of passing id from geofence as arg to be passed to location fragment
        val locationArgs = LocationFragmentArgs.Builder()
            .setLocationId(locationId).build().toBundle()

        val intent: PendingIntent = NavDeepLinkBuilder(context)
            .setGraph(R.navigation.mobile_navigation)
            .setDestination(R.id.navigation_location)
            .setArguments(locationArgs)
            .createPendingIntent()

        // for notification related message and title
        val outdoorDao = OutdoorRoomDatabase.getInstance(context).outdoorDao()
        val outdoorRepository = OutdoorRoomRepository(outdoorDao)
        val location = outdoorRepository.getLocationById(locationId)
        val message: String = (location.title)
        val notification = NotificationCompat.Builder(context, "locations")
            .setSmallIcon(R.drawable.ic_star_black_24dp)
            .setContentTitle(context.getString(R.string.notification_title))
            .setContentText(message)
            .setContentIntent(intent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)
    }
}
