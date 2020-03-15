package com.udacity.asteroidradar.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteroidsRepository
import retrofit2.HttpException


class RefreshDataWorker(appContext: Context, params: WorkerParameters):
    CoroutineWorker(appContext, params) {

    companion object {
        const val WORK_NAME = "RefreshDataWorker"
    }

    override suspend fun doWork(): Result {
        val databaseAsteroids = getDatabase(applicationContext)
        val repositoryAsteroids = AsteroidsRepository(databaseAsteroids)

        return try {
            repositoryAsteroids.refreshAsteroids()
            Result.success()
        } catch (e: HttpException) {
            Result.retry()
        }
    }

}