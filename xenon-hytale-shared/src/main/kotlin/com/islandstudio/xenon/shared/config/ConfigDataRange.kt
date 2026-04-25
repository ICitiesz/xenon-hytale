package com.islandstudio.xenon.shared.config

import com.islandstudio.xenon.shared.utils.data.DataType
import com.islandstudio.xenon.shared.utils.data.DataUtil

sealed class ConfigDataRange<T> {
    companion object {
        fun validateDataRange(value: Any, dataType: DataType, dataRange: ConfigDataRange<*>): Boolean {
            return when(dataRange) {
                is ByMinMax<*> -> {
                    DataUtil.validateDataRange(value, dataType, dataRange.min, dataRange.max)
                }

                is BySelection<*> -> {
                    dataRange.selection.contains(value)
                }

                is ByBoolean -> {
                    DataUtil.validateDataRange(value, dataType, ByBoolean.TRUE_VALUE, ByBoolean.FALSE_VALUE)
                }
            }
        }
    }

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
