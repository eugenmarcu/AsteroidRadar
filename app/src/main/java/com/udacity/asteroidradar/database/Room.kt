package com.udacity.asteroidradar.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AsteroidDao {
    @Query("select * from databaseasteroid order by closeApproachDate")
    fun getAsteroidsSaved(): LiveData<List<DatabaseAsteroid>>

    @Query("select * from databaseasteroid where closeApproachDate = :date order by closeApproachDate")
    fun getAsteroidsOnDate(date : String): LiveData<List<DatabaseAsteroid>>

    @Query("Select * from databaseasteroid where closeApproachDate >= :date ORDER by closeApproachDate")
    fun getAsteroidsWeek(date: String): LiveData<List<DatabaseAsteroid>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg videos: DatabaseAsteroid)
}

@Database(entities = [DatabaseAsteroid::class], version = 1)
abstract class AsteroidsDatabase : RoomDatabase() {

    abstract val asteroidDao: AsteroidDao
}

private lateinit var INSTANCE: AsteroidsDatabase

fun getDatabase(context: Context): AsteroidsDatabase {
    synchronized(AsteroidsDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context.applicationContext,
                AsteroidsDatabase::class.java,
                "asteroids").build()
        }
    }
    return INSTANCE
}


