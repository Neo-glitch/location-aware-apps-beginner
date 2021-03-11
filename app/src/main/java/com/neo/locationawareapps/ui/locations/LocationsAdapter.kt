package com.neo.locationawareapps.ui.locations

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.neo.locationawareapps.R
import com.neo.locationawareapps.data.Location
import kotlinx.android.synthetic.main.location_item.view.*

class LocationsAdapter(private val onClickListener: OnClickListener) :
    RecyclerView.Adapter<LocationsAdapter.LocationHolder>() {
    private var allLocations: List<Location> = ArrayList()
    private var currentLocation: android.location.Location? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.location_item, parent, false)
        return LocationHolder(itemView)
    }

    override fun getItemCount(): Int {
        return allLocations.size
    }

    fun setLocations(locations: List<Location>) {
        allLocations = locations
        notifyDataSetChanged()
    }

    /*
    sets current loc to loc param and sort all loc by distance
     */
    fun setCurrentLocation(location: android.location.Location) {
        currentLocation = location
        allLocations = allLocations.sortedBy { it.getDistanceInMiles(location) }  // gets distance btw prev and current loc
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: LocationHolder, position: Int) {
        holder.bind(allLocations[position], onClickListener)
    }

    inner class LocationHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(location: Location, clickListener: OnClickListener) {
            with(itemView) {
                title.text = location.title
                card.setOnClickListener { clickListener.onClick(location.locationId) }

                if (currentLocation != null) {
                    distanceIcon.visibility = View.VISIBLE

                    distance.visibility = View.VISIBLE
                    distance.text = context.getString(
                        R.string.distance_value,
                        location.getDistanceInMiles(currentLocation!!)
                    )
                }
            }
        }
    }

    interface OnClickListener {
        fun onClick(id: Int)
    }
}