package com.islandstudio.xenon.shared.config

abstract class BaseConfigProperty<T> {
    abstract val defaultValue: T
    abstract val dataRange: ConfigDataRange<T>
}