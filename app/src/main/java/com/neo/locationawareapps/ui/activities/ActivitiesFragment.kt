package com.neo.locationawareapps.ui.activities

import android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Build.VERSION.SDK
import android.os.Bundle
import android.provider.Settings.Global.getString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.neo.locationawareapps.GeofenceBroadcastReceiver
import com.neo.locationawareapps.R
import com.neo.locationawareapps.data.GeofencingChanges
import com.neo.locationawareapps.ui.map.MapFragment
import kotlinx.android.synthetic.main.fragment_activities.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

class ActivitiesFragment : Fragment(), ActivitiesAdapter.OnClickListener {
    private val pendingIntent: PendingIntent by lazy {
        val intent = Intent(requireContext(),
        GeofenceBroadcastReceiver::class.java)  // receiver to receive the intent when fired
        PendingIntent.getBroadcast(requireContext(), 0, intent,
        PendingIntent.FLAG_UPDATE_CURRENT)  // gen pending intent from this intent

    }
    private lateinit var activitiesViewModel: ActivitiesViewModel
    private lateinit var geofencingClient: GeofencingClient // obj handle all opp of geofencing api
    private var geofencingChanges: GeofencingChanges? = null // class in entities file that holds feofences needed to add or rem


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        inflater.inflate(R.layout.fragment_activities, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activitiesViewModel = ViewModelProvider(this)
            .get(ActivitiesViewModel::class.java)

        geofencingClient = LocationServices.getGeofencingClient(requireActivity())

        val adapter = ActivitiesAdapter(this)
        listActivities.adapter = adapter

        activitiesViewModel.allActivities.observe(viewLifecycleOwner, Observer {
            adapter.setActivities(it)
            if(it.any{a -> a.geofenceEnabled} && checkPermissions().isEmpty()){
                Snackbar.make(requireView(),
                getString(R.string.activities_background_reminder),
                Snackbar.LENGTH_LONG)
                    .setAction(R.string.ok){}
                    .show()
            }
        })
    }

    override fun onClick(id: Int, title: String) {
        // action object that conn activities frag to locations fragment
        val action = ActivitiesFragmentDirections
            .actionNavigationActivitiesToNavigationLocations()
        action.activityId = id
        action.title = "Locations with $title"
        val navController = Navigation.findNavController(requireView())
        navController.navigate(action)
    }

    override fun onGeofenceClick(id: Int) {
        geofencingChanges = activitiesViewModel.toggleGeofencing(id)
        handleGeofencing()
    }

    @SuppressLint("InlinedApi")
    @AfterPermissionGranted(RC_LOCATION)
    private fun handleGeofencing() {
        val neededPermissions = checkPermissions()  // list of permission we need
        if (neededPermissions.contains(ACCESS_FINE_LOCATION)){
            requestPermission(R.string.activities_location_snackbar,
            R.string.locations_rationale, ACCESS_FINE_LOCATION)
        } else if(neededPermissions.contains(ACCESS_BACKGROUND_LOCATION)){
            requestPermission(R.string.activities_background_snackbar,
            R.string.activities_background_rationale, ACCESS_BACKGROUND_LOCATION)
        } else {
            processGeofences()
        }
    }

    private fun requestPermission(@StringRes snackBarMessage: Int,
                                  @StringRes rationaleMessage: Int,
                                  permission: String){
        Snackbar.make(requireView(),
            getString(snackBarMessage),
            Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.ok) {EasyPermissions.requestPermissions(
                this,
                getString(rationaleMessage),  // shown on next request of perm is user says no first time
                RC_LOCATION,
                ACCESS_FINE_LOCATION
            )}
            .show()
    }

    @SuppressLint("MissingPermission")
    private fun processGeofences() {
        if(geofencingChanges != null){
            // remove old geofences
            if(geofencingChanges!!.idsToRemove.isNotEmpty()){
                geofencingClient.removeGeofences(geofencingChanges!!.idsToRemove)
            }
            // add locations to geofences
            if(geofencingChanges!!.locationsToAdd.isNotEmpty()){
                geofencingClient.addGeofences(
                    geofencingRequest(),
                    pendingIntent  // fired when fence is triggered
                )
            }
        }
    }

    private fun geofencingRequest(): GeofencingRequest? =
        GeofencingRequest.Builder()
            .apply {
                addGeofences(geofencingChanges!!.locationsToAdd)  // adds geofences created in Outdoor room repo
                setInitialTrigger(Geofence.GEOFENCE_TRANSITION_ENTER)  // trigger geo when user enters fences
            }.build()



    /*
    check if needed permission is enabled and ret permissions not enabled
     */
    private fun checkPermissions(): List<String> {
        val permissionsNeeded = ArrayList<String>()  // list of permission not granted and needed by frag
        if(!EasyPermissions.hasPermissions(requireContext(), ACCESS_FINE_LOCATION)){
            permissionsNeeded.add(ACCESS_FINE_LOCATION)
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                !EasyPermissions.hasPermissions(requireContext(), ACCESS_BACKGROUND_LOCATION)){
            // request background permission from android Q and higher since given by default to lower versions
            permissionsNeeded.add(ACCESS_BACKGROUND_LOCATION)
        }
        return permissionsNeeded
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    companion object{
        const val RC_LOCATION = 12
    }
}
