package com.farukkaradeniz.androidlistofphotos.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Faruk Karadeniz on 7.05.2018.
 * Twitter: twitter.com/Omeerfk
 * Github: github.com/FarukKaradeniz
 * LinkedIn: linkedin.com/in/FarukKaradeniz
 * Website: farukkaradeniz.com
 */
@Parcelize
data class Photo(val previewURL: String, val largeImageURL: String, val tags: String) : Parcelable