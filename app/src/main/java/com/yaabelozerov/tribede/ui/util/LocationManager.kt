package com.yaabelozerov.tribede.ui.util

import android.content.Context
import android.location.Geocoder
import java.io.IOException
import java.util.Locale

class LocationManager(
    context: Context
) {
    fun getLocationFromAddress(address: String, context: Context) {
        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocationName(address, 1)
            if (addresses != null) {
                if (addresses.isNotEmpty()) {
                    val latitude = addresses[0].latitude
                    val longitude = addresses[0].longitude
                    println("Latitude: $latitude, Longitude: $longitude")
                } else {
                    println("Адрес не найден")
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
