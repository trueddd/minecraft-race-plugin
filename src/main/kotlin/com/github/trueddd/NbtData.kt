package com.github.trueddd

sealed class NbtData(open val value: Any) {
    data class Byte(override val value: kotlin.Byte) : NbtData(value)
    data class Short(override val value: kotlin.Short) : NbtData(value)
    data class Int(override val value: kotlin.Int) : NbtData(value)
    data class Long(override val value: kotlin.Long) : NbtData(value)
    data class Float(override val value: kotlin.Float) : NbtData(value)
    data class Double(override val value: kotlin.Double) : NbtData(value)
    data class String(override val value: kotlin.String) : NbtData(value)
}
