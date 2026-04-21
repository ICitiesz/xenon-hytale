package com.islandstudio.xenon.shared.config.configproperty

import com.islandstudio.xenon.shared.config.BaseConfigEntry
import com.islandstudio.xenon.shared.config.BaseConfigSection
import com.islandstudio.xenon.shared.config.ConfigDataRange

sealed class TestConfigProperty(override val sectionKey: String): BaseConfigSection(sectionKey) {
    data object TestFeature: TestConfigProperty("TestFeature") {
        override val description: String = "This is a test feature."

        val isEnabled = object : BaseConfigEntry<Boolean>("IsEnabled") {
            override val defaultValue: Boolean = false
            override val dataRange: ConfigDataRange<Boolean> = ConfigDataRange.ByBoolean
            override val description: String = "This is a toggle for the test feature."
        }
    }

    data object TestFeatureOption: TestConfigProperty("TestFeature.Option") {
        override val description: String = "This is a test feature option."

        val testOption1 = object : BaseConfigEntry<Int>("TestOption1") {
            override val defaultValue: Int = 1
            override val dataRange: ConfigDataRange<Int> = ConfigDataRange.ByMinMax(1, 1)
            override val description: String = "This is a test option 1."
        }
    }
}