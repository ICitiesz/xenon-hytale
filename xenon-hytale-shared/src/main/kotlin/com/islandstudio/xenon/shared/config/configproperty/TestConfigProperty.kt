package com.islandstudio.xenon.shared.config.configproperty

import com.islandstudio.xenon.shared.config.AbstractConfigEntry
import com.islandstudio.xenon.shared.config.AbstractConfigSection
import com.islandstudio.xenon.shared.config.ConfigDataRange
import com.islandstudio.xenon.shared.utils.data.DataType
import kotlin.reflect.full.isSubclassOf

sealed class TestConfigProperty(override val sectionKey: String = ROOT_NODE_KEY): AbstractConfigSection(sectionKey) {
    companion object {
        fun getAllConfigSection(): List<AbstractConfigSection> {
            return TestConfigProperty::class.sealedSubclasses
                .filter { it.isSubclassOf(AbstractConfigSection::class) }
                .mapNotNull { it.objectInstance as? AbstractConfigSection }
                .toList()
        }
    }

    data object TestFeature: TestConfigProperty() {
        override val description: String = "This is a test feature."

        val isEnabled = object : AbstractConfigEntry<Boolean>("IsEnabled") {
            override val defaultValue: Boolean = false
            override val dataType: DataType = DataType.Boolean
            override val dataRange: ConfigDataRange<Boolean> = ConfigDataRange.ByBoolean
            override val description: String = "This is a toggle for the test feature."
        }
    }

    data object TestFeatureOption: TestConfigProperty("Options") {
        override val description: String = "This is a test feature option."

        val testOption1 = object : AbstractConfigEntry<Long>("TestOption1") {
            override val defaultValue: Long = 1
            override val dataType: DataType = DataType.Long
            override val dataRange: ConfigDataRange<Long> = ConfigDataRange.ByMinMax(1, 1)
            override val description: String = "This is a test option 1."
        }
    }
}