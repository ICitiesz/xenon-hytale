package com.islandstudio.xenon.shared.config.configobject

import com.hypixel.hytale.codec.KeyedCodec
import com.hypixel.hytale.codec.builder.BuilderCodec
import com.islandstudio.xenon.shared.config.configproperty.TestConfigProperty
import kotlinx.serialization.Serializable

@Suppress("PropertyName")
@Serializable
data class TestConfigObject(
    val IsEnabled: Boolean = TestConfigProperty.TestFeature.isEnabled.defaultValue
) {
    companion object: BuilderCodec.Builder<TestConfigObject>(TestConfigObject::class.java, ::TestConfigObject) {
        val CONFIG_CODEC: BuilderCodec<TestConfigObject>

        init {
            this.append(
                KeyedCodec(TestConfigProperty.TestFeature.isEnabled.entryKey, BuilderCodec.BOOLEAN),
                { testConfigObject, value -> testConfigObject.copy(IsEnabled = value) },
                { testConfigObject -> testConfigObject.IsEnabled }
            ).add()

            CONFIG_CODEC = this.build()
        }
    }
}
