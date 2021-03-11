package com.neo.locationawareapps.ui.location

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.neo.locationawareapps.R
import com.neo.locationawareapps.data.Activity
import kotlinx.android.synthetic.main.location_activity_item.view.*

class ActivitiesAdapter :
    RecyclerView.Adapter<ActivitiesAdapter.ActivityHolder>() {
    private var allActivities: List<Activity> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.location_activity_item, parent, false)
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
        holder.bind(allActivities[position])
    }

    inner class ActivityHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(activity: Activity) {
            with(itemView) {
                title.text = activity.title

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
}