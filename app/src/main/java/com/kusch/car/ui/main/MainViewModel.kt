package com.kusch.car.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlin.random.Random

data class Track(
    val angDegree: Float,
    val target: Pair<Float, Float>,
    val rotateMove: Pair<Float, Float>
)

class MainViewModel : ViewModel() {
    private var screenWidth = 0
    private var screenHeight = 0
    var ldTargetPosition = MutableLiveData<Pair<Float, Float>>()
    private var curPosition = Pair(0F, 0F)
    var ldTrack = MutableLiveData<Track>()
    private var pointCount = 1
    private var carSize = 70

    private var angDegree = 0.0.toFloat()

    private fun moveTarget() = Pair(
        carSize + Random.nextInt(screenWidth - carSize * 2).toFloat(),
        carSize + Random.nextInt(screenHeight - carSize * 2).toFloat()
    )

    fun setCarSize(carSize: Int) {
        this.carSize = carSize
    }

    fun setScreenSize(width: Int, height: Int) {
        screenWidth = width
        screenHeight = height
    }

    fun initTrack(curPosition: Pair<Float, Float>) {
        this.curPosition = curPosition
        ldTargetPosition.value = moveTarget()
        pointCount = 1
        calcTrackParams()
    }

    fun calcTrackParams() {
        val target = if (pointCount == 0) ldTargetPosition.value else moveTarget()
        var curX = curPosition.first
        var curY = curPosition.second
        var targetX = target!!.first
        var targetY = target!!.second

        val deltaX = targetX - curX
        val deltaY = targetY - curY
        val hopoten = Math.sqrt(
            Math.pow(
                (curY - targetY).toDouble(),
                2.0
            ) + Math.pow((targetX - curX).toDouble(), 2.0)
        )

        angDegree = Math.toDegrees(Math.acos((curY - targetY) / hopoten)).toFloat()
        if ((deltaX < 0 && deltaY < 0) || (deltaX < 0 && deltaY > 0))
            angDegree = -angDegree

        ldTrack.value = Track(
            angDegree,
            Pair(targetX, targetY),
            Pair((deltaX / 3.0).toFloat(), (deltaY / 3.0).toFloat())
        )
        curPosition = Pair(targetX, targetY)
    }

    fun calcNextTarget() {
        if (pointCount > 0) {
            pointCount--
            calcTrackParams()
        }
    }
}