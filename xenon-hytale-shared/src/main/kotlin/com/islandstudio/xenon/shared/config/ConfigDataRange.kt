package com.islandstudio.xenon.shared.config

sealed class ConfigDataRange<T> {

    data class ByMinMax<T>(val min: T, val max: T): ConfigDataRange<T>()

    data object ByBoolean: ConfigDataRange<Boolean>() {
        const val TRUE_VALUE: Boolean = true
        const val FALSE_VALUE: Boolean = false
    }

    data class BySelection<T>(val selection: Array<T>): ConfigDataRange<T>() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as BySelection<*>

            return selection.contentEquals(other.selection)
        }

        override fun hashCode(): Int {
            return selection.contentHashCode()
        }
    }
}
