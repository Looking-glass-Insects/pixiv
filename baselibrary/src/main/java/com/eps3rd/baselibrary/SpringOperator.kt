package com.eps3rd.baselibrary

class SpringOperator(damp: Float, response: Float) {
    private val damping: Double
    private val tension: Double
    fun updateVelocity(
        velocity: Double,
        deltaT: Float,
        targetValue: Double,
        curValue: Double
    ): Double {
        var velocity = velocity
        velocity *= 1.0 - damping * deltaT.toDouble()
        return velocity + (tension * (targetValue - curValue) * deltaT.toDouble()).toFloat()
            .toDouble()
    }

    init {
        tension = Math.pow(6.283185307179586 / response.toDouble(), 2.0)
        damping = 12.566370614359172 * damp.toDouble() / response.toDouble()
    }
}
