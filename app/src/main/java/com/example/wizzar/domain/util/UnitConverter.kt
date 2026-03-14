package com.example.wizzar.domain.util

import com.example.wizzar.data.dataSource.local.datastore.TempUnit
import com.example.wizzar.data.dataSource.local.datastore.WindUnit
import kotlin.math.round
import kotlin.times

object UnitConverter {

    fun convertTemperature(celsius: Double, targetUnit: TempUnit): Double {
        return when (targetUnit) {
            TempUnit.CELSIUS -> celsius
            TempUnit.FAHRENHEIT -> (celsius * 9 / 5) + 32
            TempUnit.KELVIN -> celsius + 273.15
        }
    }

    fun convertWindSpeed(ms: Double, targetUnit: WindUnit): Double {
        return when (targetUnit) {
            WindUnit.METER_SEC -> ms
            WindUnit.MILE_HOUR -> round(ms * 2.23694 * 100) / 100.0
        }
    }
}