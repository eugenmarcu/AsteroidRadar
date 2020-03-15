package com.udacity.asteroidradar.model

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.AsteroidListItemBinding

/**
 * This class implements a [RecyclerView] [ListAdapter] which uses Data Binding to present [List]
 * data, including computing diffs between lists.
 */
class AsteroidRecyclerViewAdapter(val onClickListener: OnClickListener) :
    ListAdapter<Asteroid, AsteroidRecyclerViewAdapter.AsteroidViewHolder>(DiffCallback) {

    /**
     * The MarsPropertyViewHolder constructor takes the binding variable from the associated
     * ListViewItem, which nicely gives it access to the full [Asteroid] information.
     */
    class AsteroidViewHolder private constructor(private val binding: AsteroidListItemBinding):
        RecyclerView.ViewHolder(binding.root) {
        fun bind(asteroid: Asteroid) {
            binding.asteroid = asteroid
            binding.asteroidItemName.text = asteroid.codename
            binding.asteroidItemDate.text = asteroid.closeApproachDate
            if (asteroid.isPotentiallyHazardous) {
                binding.asteroidStatusIcon.setImageResource(R.drawable.ic_status_potentially_hazardous)
            } else {
                binding.asteroidStatusIcon.setImageResource(R.drawable.ic_status_normal)
            }
            // This is important, because it forces the data binding to execute immediately,
            // which allows the RecyclerView to make the correct view size measurements
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): AsteroidViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = AsteroidListItemBinding.inflate(layoutInflater, parent, false)
                return AsteroidViewHolder(binding)
            }
        }
    }

    /**
     * Allows the RecyclerView to determine which items have changed when the [List] of [Asteroid]
     * has been updated.
     */
    companion object DiffCallback : DiffUtil.ItemCallback<Asteroid>() {
        override fun areItemsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
            return oldItem.id == newItem.id
        }
    }

    /**
     * Create new [RecyclerView] item views (invoked by the layout manager)
     */
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): AsteroidViewHolder {
        return AsteroidViewHolder.from(parent)
    }

    /**
     * Replaces the contents of a view (invoked by the layout manager)
     */
    override fun onBindViewHolder(holder: AsteroidViewHolder, position: Int) {
        val asteroid = getItem(position)
        holder.bind(asteroid)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(asteroid)
        }
    }

    class OnClickListener(val clickListener: (asteroid: Asteroid) -> Unit) {
        fun onClick(asteroid:Asteroid) = clickListener(asteroid)
    }
}
