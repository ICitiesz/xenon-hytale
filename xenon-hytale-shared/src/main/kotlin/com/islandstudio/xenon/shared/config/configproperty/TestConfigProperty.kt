package com.islandstudio.xenon.shared.config.configproperty

import com.islandstudio.xenon.shared.config.BaseConfigEntry
import com.islandstudio.xenon.shared.config.BaseConfigSection
import com.islandstudio.xenon.shared.config.ConfigDataRange
import com.islandstudio.xenon.shared.utils.data.DataType
import kotlin.reflect.full.isSubclassOf

sealed class TestConfigProperty(override val sectionKey: String = ROOT_NODE_KEY): BaseConfigSection(sectionKey) {
    companion object {
        fun getAllConfigSection(): List<BaseConfigSection> {
            return TestConfigProperty::class.sealedSubclasses
                .filter { it.isSubclassOf(BaseConfigSection::class) }
                .mapNotNull { it.objectInstance as? BaseConfigSection }
                .toList()
        }
    }

    data object TestFeature: TestConfigProperty() {
        override val description: String = "This is a test feature."

        val isEnabled = object : BaseConfigEntry<Boolean>("IsEnabled") {
            override val defaultValue: Boolean = false
            override val dataType: DataType = DataType.Boolean
            override val dataRange: ConfigDataRange<Boolean> = ConfigDataRange.ByBoolean
            override val description: String = "This is a toggle for the test feature."
        }
    }

    data object TestFeatureOption: TestConfigProperty("TestFeature.Option") {
        override val description: String = "This is a test feature option."

        val testOption1 = object : BaseConfigEntry<Long>("TestOption1") {
            override val defaultValue: Long = 1
            override val dataType: DataType = DataType.Long
            override val dataRange: ConfigDataRange<Long> = ConfigDataRange.ByMinMax(1, 1)
            override val description: String = "This is a test option 1."
        }
    }
}