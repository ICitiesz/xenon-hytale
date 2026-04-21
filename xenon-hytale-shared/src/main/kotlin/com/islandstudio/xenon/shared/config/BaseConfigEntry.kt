package com.islandstudio.xenon.shared.config

abstract class BaseConfigEntry<T>(val entryKey: String): IConfigDescriptor {
    abstract val defaultValue: T
    abstract val dataRange: ConfigDataRange<T>
}