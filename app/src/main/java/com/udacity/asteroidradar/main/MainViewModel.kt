package com.udacity.asteroidradar.main

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.api.*
import com.udacity.asteroidradar.model.Asteroid
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteroidsRepository
import kotlinx.coroutines.*
import retrofit2.await


class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val _pictureOfTheDay = MutableLiveData<PictureOfDay>()
    val pictureOfTheDay: LiveData<PictureOfDay>
        get() = _pictureOfTheDay

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val _navigateToSelectedAsteroid = MutableLiveData<Asteroid>()

    val navigateToSelectedProperty: LiveData<Asteroid>
        get() = _navigateToSelectedAsteroid

    private val database = getDatabase(application)

    private val asteroidsRepository = AsteroidsRepository(database)

    private val _asteroids = MutableLiveData<List<Asteroid>>()
    val asteroids: LiveData<List<Asteroid>>
        get() = _asteroids

    init {
        if (isInternetConnection(application)) {
            coroutineScope.launch {
                asteroidsRepository.refreshAsteroids()
                var getPictureOfTheDayDeferred =
                    PictureOTDApi.retrofitService.getPictureOfDay()
                _pictureOfTheDay.value = getPictureOfTheDayDeferred.await()
            }
        } else {
            Toast.makeText(
                application,
                application.getString(R.string.no_internet),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun displayAsteroidsDetails(asteroid: Asteroid) {
        _navigateToSelectedAsteroid.value = asteroid
    }

    fun displayPropertyDetailsComplete() {
        _navigateToSelectedAsteroid.value = null
    }

    fun getAsteroidsWeek(): LiveData<List<Asteroid>> {
        return asteroidsRepository.getAsteroidsWeek(getDate())
    }

    fun getAsteroidsToday(): LiveData<List<Asteroid>> {
        return asteroidsRepository.getAsteroidsOnDate(getDate())
    }

    fun getSavedAsteroids(): LiveData<List<Asteroid>> {
        return asteroidsRepository.asteroids
    }

    fun setAsteroids(asteroidList: List<Asteroid>) {
        _asteroids.value = asteroidList
    }

    /**
     * When the [ViewModel] is finished, we cancel our coroutine [viewModelJob], which tells the
     * Retrofit service to stop.
     */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}