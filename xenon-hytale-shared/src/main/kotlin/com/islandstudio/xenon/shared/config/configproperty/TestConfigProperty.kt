package com.islandstudio.xenon.shared.config.configproperty

import com.islandstudio.xenon.shared.config.BaseConfigProperty
import com.islandstudio.xenon.shared.config.ConfigDataRange

object TestConfigProperty {
    val TestFeature = object: BaseConfigProperty<Boolean>() {
        override val defaultValue: Boolean = false
        override val dataRange: ConfigDataRange<Boolean> = ConfigDataRange.ByBoolean
    }
}