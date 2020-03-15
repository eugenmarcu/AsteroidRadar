package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import com.udacity.asteroidradar.model.AsteroidRecyclerViewAdapter

class MainFragment : Fragment() {

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentMainBinding.inflate(inflater)

        binding.lifecycleOwner = this

        binding.viewModel = viewModel


        // Sets the adapter of the asteroids RecyclerView
        binding.asteroidRecycler.adapter =
            AsteroidRecyclerViewAdapter(AsteroidRecyclerViewAdapter.OnClickListener {
                viewModel.displayAsteroidsDetails(it)
            })

        viewModel.navigateToSelectedProperty.observe(viewLifecycleOwner, Observer { it ->
            if (null != it) {
                this.findNavController()
                    .navigate(MainFragmentDirections.actionMainFragmentToDetailFragment(it))
                viewModel.displayPropertyDetailsComplete()

            }
        })

        getAsteroidsWeek()

        setHasOptionsMenu(true)

        return binding.root
    }

    fun getAsteroidsWeek() {
        viewModel.getAsteroidsWeek().observe(viewLifecycleOwner, Observer {
            if (it != viewModel.asteroids.value)
                viewModel.setAsteroids(it)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.show_week -> {
                getAsteroidsWeek()
                true
            }
            R.id.show_today -> {
                viewModel.getAsteroidsToday().observe(
                    viewLifecycleOwner,
                    Observer { viewModel.setAsteroids(it) })
                true
            }
            R.id.show_saved -> {
                viewModel.getSavedAsteroids().observe(
                    viewLifecycleOwner,
                    Observer { viewModel.setAsteroids(it) })
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}
