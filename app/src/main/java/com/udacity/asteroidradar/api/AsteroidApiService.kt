package com.udacity.asteroidradar.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.BuildConfig
import com.udacity.asteroidradar.Constants.BASE_URL
import com.udacity.asteroidradar.PictureOfDay
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

enum class AsteroidsApiFilter(val value: String) { SHOW_WEEK("View week asteroids"), SHOW_TODAY("View today asteroids"), SHOW_SAVED("View saved asteroids") }

/**
 * Build the Moshi object that Retrofit will be using, making sure to add the Kotlin adapter for
 * full Kotlin compatibility.
 */
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofitScalars = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

/**
 * Use the Retrofit builder to build a retrofit object using a Moshi converter with our Moshi
 * object.
 */
private val retrofitMoshi = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL)
    .build()


interface AsteroidApiService {
    @GET("neo/rest/v1/feed")
    fun getAsteroids(
        @Query("api_key") apiKey: String = BuildConfig.ApiKey
    ): Call<String>

}

interface PictureOTDApiService {

    @GET("planetary/apod")
    fun getPictureOfDay(@Query("api_key") apiKey: String = BuildConfig.ApiKey): Call<PictureOfDay>
}

object AsteroidApi {
    val retrofitService: AsteroidApiService by lazy {
        retrofitScalars.create(AsteroidApiService::class.java)
    }
}

object PictureOTDApi {
    val retrofitService: PictureOTDApiService by lazy {
        retrofitMoshi.create(PictureOTDApiService::class.java)
    }
}