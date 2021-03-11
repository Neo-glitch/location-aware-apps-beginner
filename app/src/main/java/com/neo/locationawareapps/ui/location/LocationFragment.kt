package com.neo.locationawareapps.ui.location

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.neo.locationawareapps.R
import kotlinx.android.synthetic.main.fragment_location.*

class LocationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        inflater.inflate(R.layout.fragment_location, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val locationViewModel = ViewModelProvider(this)
            .get(LocationViewModel::class.java)

        arguments?.let { bundle ->
            val passedArguments = LocationFragmentArgs.fromBundle(bundle)
            locationViewModel.getLocation(passedArguments.locationId)
                .observe(viewLifecycleOwner, Observer { wrapper ->
                    val location = wrapper.location
                    title.text = location.title
                    hours.text = location.hours
                    description.text = location.description
                    val adapter = ActivitiesAdapter()
                    listActivities.adapter = adapter
                    adapter.setActivities(wrapper.activities.sortedBy { a -> a.title })
                })
        }
    }
}
