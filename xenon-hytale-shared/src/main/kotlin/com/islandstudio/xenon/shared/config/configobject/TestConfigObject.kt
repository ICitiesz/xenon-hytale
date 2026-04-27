package com.islandstudio.xenon.shared.config.configobject

import com.hypixel.hytale.codec.KeyedCodec
import com.hypixel.hytale.codec.builder.BuilderCodec
import com.islandstudio.xenon.shared.config.configproperty.TestConfigProperty
import kotlinx.serialization.Serializable

@Suppress("PropertyName")
@Serializable
data class TestConfigObject(
    val IsEnabled: Boolean = TestConfigProperty.TestFeature.isEnabled.defaultValue,
    val Options: TestOptions = TestOptions()
) {
    companion object: BuilderCodec.Builder<TestConfigObject>(TestConfigObject::class.java, ::TestConfigObject) {
        val CONFIG_CODEC: BuilderCodec<TestConfigObject>

        init {
            this.append(
                KeyedCodec(TestConfigProperty.TestFeature.isEnabled.entryKey, BuilderCodec.BOOLEAN),
                { testConfigObject, value -> testConfigObject.copy(IsEnabled = value) },
                { testConfigObject -> testConfigObject.IsEnabled }
            ).add()

            this.append(
                KeyedCodec(TestConfigProperty.TestFeatureOption.testOption1.entryKey, BuilderCodec.LONG),
                { testConfigObject, value -> testConfigObject.copy(Options = testConfigObject.Options.copy(TestOption1 = value)) },
                { testConfigObject -> testConfigObject.Options.TestOption1 }
            ).add()

            CONFIG_CODEC = this.build()
        }
    }

    @Serializable
    data class TestOptions(
        val TestOption1: Long = TestConfigProperty.TestFeatureOption.testOption1.defaultValue,
        val TestModel: TestModel = TestModel()
    )

    @Serializable
    data class TestModel(
        val TestModelOption1: String = "yes"
    )
}
