package com.islandstudio.xenon.shared.config.configobject

import com.islandstudio.xenon.shared.config.configproperty.TestConfigProperty
import kotlinx.serialization.Serializable

@Serializable
data class TestConfigObject(
    val testValue1: Boolean = TestConfigProperty.TestFeature.defaultValue
)
