package com.projects.razvan.serraproject

import android.support.annotation.DrawableRes
import android.support.annotation.StringRes

/**
 * Created by Razvan on 1/21/2018.
 */
enum class Control(@StringRes val description: Int, @DrawableRes val icon: Int, val maxValue: Int){
    light(R.string.light,R.drawable.ic_light,255),
    temperature(R.string.temperature,R.drawable.ic_temp_c,50),
    humidity(R.string.humidity,R.drawable.ic_humidity,100)
}