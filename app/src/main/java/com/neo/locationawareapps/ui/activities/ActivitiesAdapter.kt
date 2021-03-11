package com.neo.locationawareapps.ui.activities

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.neo.locationawareapps.R
import com.neo.locationawareapps.data.Activity
import kotlinx.android.synthetic.main.activity_item.view.*

class ActivitiesAdapter(private val onClickListener: OnClickListener) :
    RecyclerView.Adapter<ActivitiesAdapter.ActivityHolder>() {
    private var allActivities: List<Activity> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_item, parent, false)
        return ActivityHolder(itemView)
    }

    override fun getItemCount(): Int {
        return allActivities.size
    }

    fun setActivities(activities: List<Activity>) {
        allActivities = activities
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ActivityHolder, position: Int) {
        holder.bind(allActivities[position], onClickListener)
    }

    inner class ActivityHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(activity: Activity, clickListener: OnClickListener) {
            // With block is makes binding of widgets of itemView easier
            with(itemView) {
                title.text = activity.title

                card.setOnClickListener {
                    clickListener.onClick(activity.activityId, activity.title)
                }
                geofence.setOnClickListener { clickListener.onGeofenceClick(activity.activityId) }

                var color = R.color.colorGray
                if (activity.geofenceEnabled) {
                    color = R.color.colorAccent
                }

                ImageViewCompat
                    .setImageTintList(
                        geofence,
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                geofence.context,
                                color
                            )
                        )
                    )

                val iconUri = "drawable/ic_${activity.icon}_black_24dp"
                val imageResource: Int =
                    context.resources.getIdentifier(
                        iconUri, null, context.packageName
                    )
                icon.setImageResource(imageResource)
                icon.contentDescription = activity.title
            }
        }
    }

    interface OnClickListener {
        fun onClick(id: Int, title: String)
        fun onGeofenceClick(id: Int)
    }
}