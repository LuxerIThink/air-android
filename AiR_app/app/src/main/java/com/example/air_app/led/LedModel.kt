package com.example.air_app.led

class LedModel
{
    var R: Int? = null
    var G: Int? = null
    var B: Int? = null


    fun colorNotNull(): Boolean {
        return (R != null) and (G != null) and (B != null)
    }

    fun clear() {
        R = null
        G = null
        B = null
    }

    private fun getR(): Int {
        return R ?: 0
    }

    private fun getG(): Int {
        return G ?: 0
    }

    private fun getB(): Int {
        return B ?: 0
    }

    val color: Int
        get() {
            val r = getR()
            val g = getG()
            val b = getB()
            val a = (r + g + b) / 3
            return a and 0xff shl 24 or (r and 0xff shl 16) or (g and 0xff shl 8) or (b and 0xff)
        }

    fun setColor(mdl: LedModel) {
        R = mdl.getR()
        G = mdl.getG()
        B = mdl.getB()
    }
}