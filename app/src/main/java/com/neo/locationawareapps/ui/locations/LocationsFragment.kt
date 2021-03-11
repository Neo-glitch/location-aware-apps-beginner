package com.neo.locationawareapps.ui.locations

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.neo.locationawareapps.R
import kotlinx.android.synthetic.main.fragment_locations.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

class LocationsFragment : Fragment(), LocationsAdapter.OnClickListener {
    private lateinit var adapter: LocationsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        inflater.inflate(R.layout.fragment_locations, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val locationsViewModel = ViewModelProvider(this)
            .get(LocationsViewModel::class.java)

        adapter = LocationsAdapter(this)
        listLocations.adapter = adapter

        arguments?.let { bundle ->
            val passedArguments = LocationsFragmentArgs.fromBundle(bundle)
            if (passedArguments.activityId == 0) {
                locationsViewModel.allLocations.observe(viewLifecycleOwner, Observer {
                    adapter.setLocations(it)
                })
            } else {
                locationsViewModel.locationsWithActivity(passedArguments.activityId)
                    .observe(viewLifecycleOwner, Observer {
                        adapter.setLocations(it.locations)
                    })
            }
        }

        getCurrentLocation()
    }


    /*
    fun to get current user's location
     */
    @SuppressLint("MissingPermission")
    @AfterPermissionGranted(RC_LOCATION)
    private fun getCurrentLocation() {
        if(EasyPermissions.hasPermissions(requireContext(),
            android.Manifest.permission.ACCESS_FINE_LOCATION)){
            // obj to get location data from
            val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
            fusedLocationProviderClient.lastLocation.addOnSuccessListener {location: Location? ->
                if(location != null){
                    // past last location to adapter to perform dist calc
                    adapter.setCurrentLocation(location)
                }
            }
        } else {
            Snackbar.make(requireView(),
            getString(R.string.locations_snackbar),
            Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok) {EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.locations_rationale),  // shown on next request of perm is user says no first time
                    RC_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )}
                .show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onClick(id: Int) {
        val action = LocationsFragmentDirections
            .actionNavigationLocationsToNavigationLocation()
        action.locationId = id
        val navController = Navigation.findNavController(requireView())
        navController.navigate(action)
    }


    companion object{
        const val RC_LOCATION = 10
    }
}
