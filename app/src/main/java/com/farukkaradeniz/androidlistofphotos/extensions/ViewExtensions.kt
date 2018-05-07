package com.farukkaradeniz.androidlistofphotos.extensions

import android.widget.ImageView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

/**
 * Created by Faruk Karadeniz on 7.05.2018.
 * Twitter: twitter.com/Omeerfk
 * Github: github.com/FarukKaradeniz
 * LinkedIn: linkedin.com/in/FarukKaradeniz
 * Website: farukkaradeniz.com
 */

//ImageView extensions
fun ImageView.setImage(imageUrl: String) {
    Picasso.get().load(imageUrl).into(this)
}

fun ImageView.setImage(imageUrl: String, callback: Callback) {
    Picasso.get().load(imageUrl).into(this, callback)
}