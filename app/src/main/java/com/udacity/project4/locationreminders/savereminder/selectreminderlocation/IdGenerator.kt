package com.udacity.project4.locationreminders.savereminder.selectreminderlocation

object IdGenerator {
    var id = 0

    fun getNextId() = id++.toString()
}
