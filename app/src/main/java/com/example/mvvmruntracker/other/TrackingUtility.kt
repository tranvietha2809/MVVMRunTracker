package com.example.mvvmruntracker.other

import android.Manifest
import android.content.Context
import android.location.Location
import android.os.Build
import com.example.mvvmruntracker.services.Polyline
import pub.devrel.easypermissions.EasyPermissions
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.concurrent.TimeUnit

object TrackingUtility {
    fun hasLocationPermissions(context: Context) =
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        } else {
            EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }

    fun hasBackgroundLocationPermission(context: Context) =
        EasyPermissions.hasPermissions(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION)

    fun getFormattedStopWatchTime(ms: Long, includeMillis: Boolean = false): String {
        var milliSeconds = ms
        val hours = TimeUnit.MILLISECONDS.toHours(milliSeconds)
        milliSeconds -= TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliSeconds)
        milliSeconds -= TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliSeconds)
        val f: NumberFormat = DecimalFormat("00")
        if (!includeMillis) {
            return "${f.format(hours)}:${f.format(minutes)}:${f.format(seconds)}"
        }
        milliSeconds -= TimeUnit.SECONDS.toMillis(seconds)
        milliSeconds /= 10
        return "${f.format(hours)}:${f.format(minutes)}:${f.format(seconds)}:${f.format(milliSeconds)}"
    }

    fun polylineToLength(polyline: Polyline): Float {
        var distance = 0f
        for (i in 0..polyline.size - 2) {
            val pos1 = polyline[i]
            val pos2 = polyline[i + 1]

            val result = FloatArray(1)
            Location.distanceBetween(
                pos1.latitude,
                pos1.longitude,
                pos2.latitude,
                pos2.longitude,
                result
            )
            distance += result[0]
        }
        return distance
    }
}