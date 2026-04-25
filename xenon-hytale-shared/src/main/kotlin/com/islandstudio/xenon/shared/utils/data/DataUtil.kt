package com.islandstudio.xenon.shared.utils.data

import com.islandstudio.xenon.shared.exception.XenonException

object DataUtil {
    fun toDataType(inputValue: Any, dataType: DataType): Any? {
        return toDataType(inputValue.toString(), dataType)
    }

    fun toDataType(inputValue: String, dataType: DataType): Any? {
        when (dataType) {
            DataType.Boolean -> {
                return inputValue.lowercase().toBooleanStrictOrNull()
            }

            DataType.Double -> {
                return inputValue.toDoubleOrNull()
            }

            DataType.Integer -> {
                return inputValue.toIntOrNull()
            }

            DataType.String -> {
                return inputValue
            }

            DataType.Long -> {
                return inputValue.toLongOrNull()
            }
        }
    }

    fun validateDataRange(inputValue: Any, dataType: DataType, minValue: Any? = null, maxValue: Any? = null): Boolean {
        when(dataType) {
            DataType.Boolean  -> {
                return inputValue.toString().lowercase().toBooleanStrictOrNull()?.let { true } ?: false
            }

            DataType.Double -> {
                val doubleValue = inputValue.toString().toDoubleOrNull() ?: return false

                return !(doubleValue < minValue.toString().toDouble() || doubleValue > maxValue.toString().toDouble())
            }

            DataType.Integer -> {
                val integerValue =  inputValue.toString().toIntOrNull() ?: return false

                return !(integerValue < minValue.toString().toInt() || integerValue > maxValue.toString().toInt())
            }

            DataType.Long -> {
                val longValue = inputValue.toString().toLongOrNull() ?: return false

                return !(longValue < minValue.toString().toLong() || longValue > maxValue.toString().toLong())
            }

            else -> {
                return false
            }
        }
    }

    inline fun <reified T> asType(value: Any): T {
        if (value !is T) throw XenonException("Error while trying to cast value, ${value::class.java.name} as ${T::class.java.name}!")

        return value
    }
}