package com.islandstudio.xenon.shared.config

import com.islandstudio.xenon.shared.utils.data.DataType

abstract class BaseConfigEntry<T>(val entryKey: String): IConfigDescriptor {
    abstract val defaultValue: T
    abstract val dataType: DataType
    abstract val dataRange: ConfigDataRange<T>
}