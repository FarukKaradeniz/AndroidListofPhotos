package com.farukkaradeniz.androidlistofphotos.api

import com.farukkaradeniz.androidlistofphotos.model.PixabayResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by Faruk Karadeniz on 7.05.2018.
 * Twitter: twitter.com/Omeerfk
 * Github: github.com/FarukKaradeniz
 * LinkedIn: linkedin.com/in/FarukKaradeniz
 * Website: farukkaradeniz.com
 */
interface PixabayApi {

    @GET("api/")
    fun getPhotos(
            @Query("key") key: String = "8909002-6f0245ffb49f5daf78913bd10",
            @Query("image_type") imageType: String = "photo",
            @Query("q") searchQuery: String,
            @Query("page") page: Int
    ): Call<PixabayResponse>

    companion object {
        private val baseUrl: String = "https://pixabay.com/"
        private val api: PixabayApi? = null
        fun getPixabayApi(): PixabayApi {
            return api ?: Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(PixabayApi::class.java)
        }

    }
}