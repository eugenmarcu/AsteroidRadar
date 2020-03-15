package com.udacity.asteroidradar.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidsDatabase
import com.udacity.asteroidradar.database.DatabaseAsteroid
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.model.Asteroid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.await
import java.lang.Exception

class AsteroidsRepository(private val database: AsteroidsDatabase) {
    val asteroids: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getAsteroidsSaved()) {
            it.asDomainModel()
        }

    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            try {
                val listResult = AsteroidApi.retrofitService.getAsteroids().await()

                val asteroidsFromNetwork = parseAsteroidsJsonResult(JSONObject(listResult))
                database.asteroidDao.insertAll(*asteroidsFromNetwork.map {
                    DatabaseAsteroid(
                        id = it.id,
                        codename = it.codename,
                        closeApproachDate = it.closeApproachDate,
                        absoluteMagnitude = it.absoluteMagnitude,
                        estimatedDiameter = it.estimatedDiameter,
                        relativeVelocity = it.relativeVelocity,
                        distanceFromEarth = it.distanceFromEarth,
                        isPotentiallyHazardous = it.isPotentiallyHazardous
                    )
                }.toTypedArray())
            } catch (e: Exception) {
                Log.e("Exception", e.message)
            }
        }

    }

    fun getAsteroidsWeek(date: String): LiveData<List<Asteroid>> {
        return Transformations.map(database.asteroidDao.getAsteroidsWeek(date)) {
            it.asDomainModel()
        }
    }

    fun getAsteroidsOnDate(date: String): LiveData<List<Asteroid>> {
        return Transformations.map(database.asteroidDao.getAsteroidsOnDate(date)) {
            it.asDomainModel()
        }
    }
}