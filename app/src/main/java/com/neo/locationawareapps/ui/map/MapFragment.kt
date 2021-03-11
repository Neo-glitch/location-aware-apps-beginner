package com.neo.locationawareapps.ui.map

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import com.neo.locationawareapps.BuildConfig
import com.neo.locationawareapps.R
import com.neo.locationawareapps.ui.locations.LocationsFragment
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions


class MapFragment : Fragment() {
    private lateinit var googleMap: GoogleMap

    /**
     * n.b: To avoid errors for myLocation functionality, deny permissions in locationsFragment
     * i.e uninstall and install app to do this
     */

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_map, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val mapViewModel = ViewModelProvider(this)
            .get(MapViewModel::class.java)

        // stores mapFragment in var to use all fun of map on this obj
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        // loads a map Aysnc
        mapFragment.getMapAsync { map ->
            googleMap = map
            enableMyLocation()
            // centers map in sanfransico using lat and long
            val bay = LatLng(37.68, -122.42)
            map.moveCamera(CameraUpdateFactory.zoomTo(10f))  // sets the zoom level of map cam
            map.moveCamera(CameraUpdateFactory.newLatLng(bay))

            // sets some ui settings of map
            map.uiSettings.isZoomControlsEnabled = true
            map.uiSettings.isTiltGesturesEnabled = false


            mapViewModel.allLocations.observe(viewLifecycleOwner, Observer {
               for (location in it){
                   val point = LatLng(location.latitude, location.longitude)
                   // adds marker to location in focus
                   val marker = map.addMarker(MarkerOptions()
                       .position(point)
                       .title(location.title)
                       .snippet("Hours: ${location.hours}")
                       .icon(getBitmapFromVector(R.drawable.ic_star_black_24dp, R.color.colorAccent))
                       .alpha(.75f))

                   marker.tag = location.locationId  // tag a marker in focus with location id in focus

                   // adds circle geofence to each marker
                   if(BuildConfig.DEBUG){
                       map.addCircle(
                           CircleOptions()
                               .center(point)
                               .radius(location.geofenceRadius.toDouble())
                       )
                   }
               }
            })

            // listener for InfoWindow of a marker
            map.setOnInfoWindowClickListener {marker ->
                val action = MapFragmentDirections.actionNavigationMapToNavigationLocation()
                action.locationId = marker.tag as Int
                val navigationController = Navigation.findNavController(requireView())
                navigationController.navigate(action)
            }
        }
//        enableMyLocation()
    }

    @SuppressLint("MissingPermission")
    @AfterPermissionGranted(RC_MAP)
    private fun enableMyLocation() {
        if(EasyPermissions.hasPermissions(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)){
//            googleMap.isMyLocationEnabled = true // specify to show myLocation functionality
            googleMap?.let {
                it.isMyLocationEnabled  = true
            }
        } else { // perm hasn't been granted yet
            Snackbar.make(requireView(),
                getString(R.string.map_snackbar),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok) {EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.map_snackbar),  // shown on next request of perm is user says no first time
                    RC_MAP,
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


    /**
     * fun takes a vector or drawable and color to tint vector to
     * and ret bitmap descriptor
     */
    private fun getBitmapFromVector(
        @DrawableRes vectorResourceId: Int,
        @ColorRes colorResourceId: Int
    ): BitmapDescriptor {
        val vectorDrawable = resources.getDrawable(vectorResourceId, requireContext().theme)
            ?: return BitmapDescriptorFactory.defaultMarker()

        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        DrawableCompat.setTint(
            vectorDrawable,
            ResourcesCompat.getColor(
                resources,
                colorResourceId, requireContext().theme
            )
        )
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    companion object{
        const val RC_MAP = 11
    }
}
